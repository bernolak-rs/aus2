package POIApp.model;

import java.time.LocalDateTime;

public class Poi {
    private final long id;
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private String address;
    private final LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private boolean deleted = false;

    public Poi(long id, String name, double latitude, double longitude, String description, String address, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.address = address;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void delete() {
        deleted = true;
    }

    public void update(String name, String desc, double lat, double lon, String address) {
        this.name = name;
        this.description = desc;
        this.latitude = lat;
        this.longitude = lon;
        this.address = address;
        this.updatedTime = LocalDateTime.now();
    }
}
