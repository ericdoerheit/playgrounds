package de.ericdoerheit;

/**
 * Created by ericdoerheit on 18/06/16.
 */
public class CarLog {
    private Long carId;
    private Long timestamp;
    private Double longitude;
    private Double latitude;
    private Double distanceLeft;
    private Double distanceRight;
    private Integer laneNumber;
    private Double direction;

    public CarLog() {
    }

    public CarLog(Long carId, Long timestamp, Double longitude, Double latitude, Double distanceLeft,
                  Double distanceRight, Integer laneNumber, Double direction) {
        this.carId = carId;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distanceLeft = distanceLeft;
        this.distanceRight = distanceRight;
        this.laneNumber = laneNumber;
        this.direction = direction;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistanceLeft() {
        return distanceLeft;
    }

    public void setDistanceLeft(Double distanceLeft) {
        this.distanceLeft = distanceLeft;
    }

    public Double getDistanceRight() {
        return distanceRight;
    }

    public void setDistanceRight(Double distanceRight) {
        this.distanceRight = distanceRight;
    }

    public Integer getLaneNumber() {
        return laneNumber;
    }

    public void setLaneNumber(Integer laneNumber) {
        this.laneNumber = laneNumber;
    }

    public Double getDirection() {
        return direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "CarLog{" +
                "carId=" + carId +
                ", timestamp=" + timestamp +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", distanceLeft=" + distanceLeft +
                ", distanceRight=" + distanceRight +
                ", laneNumber=" + laneNumber +
                ", direction=" + direction +
                '}';
    }
}
