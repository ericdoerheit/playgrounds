package de.ericdoerheit;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.*;

public class CarLogLaneInferrer {

    private static final int WINDOW_SIZE = 50;
    private static final double TO_RADIANT = Math.PI / 180;

    // One degree latitude is 111,320 meters => one meter = 1 / 111,320 degrees
    private static final double LATITUDE_TO_METERS = 111132.954;
    private static final double METERS_TO_LATITUDE = 1 / LATITUDE_TO_METERS;

    // One degree longitude is 111,320 meters * cos(latitude) => one meter = 1 / (111,320 * cos(latitude)) degrees
    private static final double LONGITUDE_TO_METERS = 77000;//111320;
    private static final double METERS_TO_LONGITUDE = 1 / LONGITUDE_TO_METERS;

    public static void main(String[] args) throws Exception {

        if (args.length != 2){
            System.err.println("usage: CarLogInferrer <log-file-path> <out-path>");
            return;
        }

        String logFilePath = args[0];
        String outPath = args[1];

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment
                .getExecutionEnvironment();

        // get input data
        DataStream<String> logs = env.readTextFile(logFilePath);
        //logs.print();

        // carId, timestamp, longitude, latitude, distanceLeft, distanceRight, laneNumber, Direction
        DataStream<CarLog> logTuples =
                logs.flatMap(new FlatMapFunction<String, CarLog>() {
                    @Override
                    public void flatMap(String s, Collector<CarLog> collector) throws Exception {
                        s = s.replaceAll(" ", "");
                        String[] splitForCarId = s.split(":");
                        Long carId = Long.valueOf(splitForCarId[0]);
                        String[] splitForValues = splitForCarId[1].split(",");

                        Long timestamp = Long.parseLong(splitForValues[0]);
                        Double longitude = Double.parseDouble(splitForValues[2]);
                        Double latitude= Double.parseDouble(splitForValues[1]);
                        Double distanceLeft= Double.parseDouble(splitForValues[3]);
                        Double distanceRight= Double.parseDouble(splitForValues[4]);
                        Integer laneNumber= Integer.parseInt(splitForValues[5]);
                        Double direction= Double.parseDouble(splitForValues[6]);

                        collector.collect(new CarLog(carId, timestamp, longitude, latitude, distanceLeft, distanceRight, laneNumber, direction));
                    }
                });

        DataStream<Tuple3<Integer, Double, Double>> carNumber = logTuples
                .flatMap(new FlatMapFunction<CarLog, LaneBorderPoint>() {
                    @Override
                    public void flatMap(CarLog carLog, Collector<LaneBorderPoint> collector) throws Exception {

                        Double leftAlpha = TO_RADIANT * (carLog.getDirection() - 90);
                        Double rightAlpha = TO_RADIANT * (carLog.getDirection() + 90);

                        Double leftLongitude = carLog.getLongitude() + carLog.getDistanceLeft()
                                * Math.cos(leftAlpha) * METERS_TO_LONGITUDE / Math.cos(carLog.getLatitude());

                        Double leftLatitude = carLog.getLatitude() + carLog.getDistanceLeft()
                                * Math.sin(leftAlpha) * METERS_TO_LATITUDE;

                        Double rightLongitude = carLog.getLongitude()+ carLog.getDistanceLeft()
                                * Math.cos(rightAlpha) * METERS_TO_LONGITUDE / Math.cos(carLog.getLatitude());

                        Double rightLatitude = carLog.getLatitude() + carLog.getDistanceLeft()
                                * Math.sin(rightAlpha) * METERS_TO_LATITUDE;

                        LaneBorderPoint left = new LaneBorderPoint(carLog.getCarId(), carLog.getTimestamp(),
                                leftLongitude, leftLatitude, carLog.getLaneNumber(), carLog.getDirection(), 1.0);
                        LaneBorderPoint right = new LaneBorderPoint(carLog.getCarId(), carLog.getTimestamp(),
                                rightLongitude, rightLatitude, carLog.getLaneNumber() + 1, carLog.getDirection(), 1.0);

                        collector.collect(left);
                        collector.collect(right);
                    }
                })
                // Key by laneNumber
                .keyBy("borderNumber")
                // Receive WINDOW_SIZE log entries with particular laneNumber
                .countWindow(WINDOW_SIZE)
                .apply(new WindowFunction<LaneBorderPoint, Tuple3<Integer, Double, Double>, Tuple, GlobalWindow>() {
                    @Override
                    public void apply(Tuple tuple, GlobalWindow globalWindow, Iterable<LaneBorderPoint> iterable, Collector<Tuple3<Integer, Double, Double>> collector) throws Exception {
                        // Count number of different cars (carIds)
                        HashSet<Long> carIds = new HashSet<>();
                        LinkedList<LaneBorderPoint> points = new LinkedList<>();
                        for(LaneBorderPoint laneBorderPoint : iterable) {
                            carIds.add(laneBorderPoint.getCarId());
                            points.add(laneBorderPoint);
                        }

                        int numberOfPoints = WINDOW_SIZE;
                        int numberOfCars = carIds.size();
                        carIds = null;

                        int numberOfIterations = 10; //(int) Math.floor(Math.log(numberOfPoints / numberOfCars)/Math.log(2));

                        double proximity = 0.25;
                        double dProximity = 0.25;
                        for (int i = 0; i < numberOfIterations; i++) {
                            LinkedList<LaneBorderPoint> newPoints = new LinkedList<>();
                            LinkedList<LaneBorderPoint> mergedPoints = new LinkedList<>();
                            for (Iterator<LaneBorderPoint> iterator = points.iterator(); iterator.hasNext(); ) {
                                LaneBorderPoint point = iterator.next();

                                // Only regard points that are not yet merged / regarded
                                if (!mergedPoints.contains(point)) {
                                    List<LaneBorderPoint> nearPoints = findNearPoints(proximity, point, points);
                                    // Add near points to merged points
                                    mergedPoints.addAll(nearPoints);

                                    // Merge near points and point into one point
                                    nearPoints.add(point);
                                    LaneBorderPoint newPoint = mergePoints(nearPoints);
                                    newPoints.add(newPoint);

                                    // Remove current point
                                    iterator.remove();
                                }
                            }
                            points = new LinkedList<LaneBorderPoint>(newPoints);

                            mergedPoints.clear();
                            newPoints.clear();

                            proximity += dProximity;
                        }

                        // Collect all points with weight > 1.0 (other points were never merged)
                        System.out.println(points.size());
                        for (LaneBorderPoint point : points) {
                            if (point.getWeight() > 1.0) {
                                collector.collect(new Tuple3<Integer, Double, Double>(point.getBorderNumber(),
                                        point.getLongitude(), point.getLatitude()));
                            }
                        }

                        // TODO Implement sorting
                    }
                });

        carNumber.writeAsCsv(outPath);

        // execute program
        env.execute("Car Log Lane Inferrer");
    }

    private static LaneBorderPoint mergePoints(List<LaneBorderPoint> points) {

        double weightSum = 0.0;
        LaneBorderPoint newPoint = points.get(0);
        double lon = 0.0;
        double lat = 0.0;

        for (LaneBorderPoint point : points) {
            weightSum += point.getWeight();
        }

        for (LaneBorderPoint point : points) {
            lon += point.getWeight() * point.getLongitude();
            lat += point.getWeight() * point.getLatitude();
        }

        lon /= weightSum;
        lat /= weightSum;

        newPoint.setLongitude(lon);
        newPoint.setLatitude(lat);
        newPoint.setWeight(weightSum);
        return newPoint;
    }

    private static List<LaneBorderPoint> findNearPoints(Double proximity, LaneBorderPoint centralPoint, List<LaneBorderPoint> points) {
        LinkedList<LaneBorderPoint> nearPoints = new LinkedList<>();

        for (LaneBorderPoint point : points) {
            double distance = distance(centralPoint.getLongitude(), centralPoint.getLatitude(),
                    point.getLongitude(), point.getLatitude());
            if (distance > 0.0 && distance <= proximity) {
                nearPoints.add(point);
            }
        }

        return nearPoints;
    }

    private static double distance(double lon1, double lat1, double lon2, double lat2) {
        double dx = (lon1 - lon2) * LONGITUDE_TO_METERS * Math.cos(lat1*TO_RADIANT);
        double dy = (lat1 - lat2)  * LATITUDE_TO_METERS;

        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist;
    }
}
