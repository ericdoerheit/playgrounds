package de.ericdoerheit;

/**
 * Created by ericdoerheit on 18/06/16.
 */
public class LaneBorderPoint {
    private Long carId;
    private Long timestamp;
    private Double longitude;
    private Double latitude;
    private Integer borderNumber;
    private Double direction;
    private Double weight;

    public LaneBorderPoint() {
    }

    public LaneBorderPoint(Long carId, Long timestamp, Double longitude, Double latitude, Integer borderNumber,
                           Double direction, Double weight) {
        this.carId = carId;
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.borderNumber = borderNumber;
        this.direction = direction;
        this.weight = weight;
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

    public Integer getBorderNumber() {
        return borderNumber;
    }

    public void setBorderNumber(Integer borderNumber) {
        this.borderNumber = borderNumber;
    }

    public Double getDirection() {
        return direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "LaneBorderPoint{" +
                "carId=" + carId +
                ", timestamp=" + timestamp +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", borderNumber=" + borderNumber +
                ", direction=" + direction +
                ", weight=" + weight +
                '}';
    }
}
