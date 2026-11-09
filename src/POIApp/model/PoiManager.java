package POIApp.model;

import POIApp.model.trees.IPoiData;
import POIApp.model.trees.KDTreeData;
import POIApp.model.trees.QuadKDTreeData;
import POIApp.model.trees.QuadTreeData;

import java.io.*;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * PoiManager is a model and in memory database used by application
 * Manages operations on data
 */
public class PoiManager {
    /**
     * Enum used to determine Tree structure
     */
    public enum TreeType {
        KDTree,
        QuadTree,
        QuadKDTree
    }

    private IPoiData poiData;
    private long nextId;
    private TreeType currentTree;

    /**
     * Creates PoiManager
     * @param treeType passes initial tree structure used to store POI data
     */
    public PoiManager(TreeType treeType) {
        this.currentTree = treeType;
        this.poiData = this.initTree(treeType);
        this.nextId = 1;
    }

    /**
     * @return all POI in tree database
     */
    public List<Poi> getAll() {
        return this.poiData.find(-90d, 90d, -180d, 180d);
    }

    /**
     * Finds all POIs matching parameters
     * @param lat latitude of a POI
     * @param lon longitude of a POI
     * @return List of POIs matching coordinates
     */
    public List<Poi> findAtPosition(double lat, double lon) {
        return poiData.find(lat, lat, lon, lon);
    }

    /**
     * AI DOKUMENTACIA 4.4
     * 
     * Finds all POIs within bounds and returns all that match name substring
     * Non-case-sensitive and ignores diacritical marks
     * @param minLat minimal latitude
     * @param maxLat maximal latitude
     * @param minLon minimal longitude
     * @param maxLon maximal longitude
     * @param name
     * @return returns List of POIs that match criteria
     */
    public List<Poi> findByName(double minLat, double maxLat, double minLon, double maxLon, String name) {
        List<Poi> rectPois = this.poiData.find(minLat, maxLat, minLon, maxLon);

        String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");

        return rectPois.stream().filter(
                        poi -> Normalizer.normalize(poi.getName(), Normalizer.Form.NFD).replaceAll("\\p{M}+", "").contains(normalizedName))
                .toList();
    }

    /**
     * AI DOKUMENTACIA 4.1, 4.3
     * 
     * Finds POIs within selected radius of a selected point
     * First finds POIs in rectangle then returns POIs only in Radius
     * Uses Haversine formula to calculate distance between points
     * @param lat latitude of center of a circle
     * @param lon longitude of center of a circle
     * @param radiusInMeters radius of a circle
     * @return POIs lying with circle
     */
    public List<Poi> findInRadius(double lat, double lon, double radiusInMeters) {
        double earthRadius = 6_371_000d;
        double latRad = Math.toRadians(lat);
        double angularRadius = radiusInMeters / earthRadius;

        double deltaLat = Math.toDegrees(angularRadius);
        double deltaLon = Math.toDegrees(
            Math.asin(Math.sin(angularRadius) / Math.cos(latRad))
        );

        double minLat = lat - deltaLat;
        double maxLat = lat + deltaLat;
        double minLon = lon - deltaLon;
        double maxLon = lon + deltaLon;

        List<Poi> rectPois = this.poiData.find(minLat, maxLat, minLon, maxLon);

        return rectPois.stream().filter(
                poi -> distanceInMeters(lat, lon, poi.getLatitude(), poi.getLongitude()) <= radiusInMeters)
                .toList();
    }

    /**
     * AI DOKUMENTACIA 4.1, 4.2
     * 
     * Calculates distance between points using Haversine formula
     * @param lat1 latitude of first point
     * @param lon1 longitude of first point
     * @param lat2 latitude of second point
     * @param lon2 longitude of second point
     * @return distance between points in meters
     */
    private double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        // Calculation of distance with haversine formula
        final double earth_radius_meters = 6_371_000d;

        double latDif = Math.toRadians(lat2 - lat1);
        double lonDif = Math.toRadians(lon2 - lon1);

        double sinLat = Math.sin(latDif / 2);
        double sinLon = Math.sin(lonDif / 2);

        double a = sinLat * sinLat
                + Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lat1)) * (sinLon * sinLon);

        // Check for double rounding error in sqrt function
        a = a > 1 ? 1 : a;
        a = a < 0 ? 0 : a;

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earth_radius_meters * c;
    }

    /**
     * Creates POI and inserts it into Tree data structure
     * @param name
     * @param desc
     * @param lat
     * @param lon
     * @param address
     * @return true if insertion was completed successfully otherwise false
     */
    public boolean addPoi(String name, String desc, double lat, double lon, String address) {
        Poi poi = new Poi(this.nextId, name, lat, lon, desc, address, LocalDateTime.now(), LocalDateTime.now());

        try {
            this.poiData.insert(poi);
            this.nextId++;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Updates already existing POI
     * @param oldPoi
     * @param newName
     * @param newDesc
     * @param newLat
     * @param newLon
     * @param newAddress
     * @return true if update was completed successfully otherwise false
     */
    public boolean updatePoi(Poi oldPoi, String newName, String newDesc, double newLat, double newLon, String newAddress) {
        if (oldPoi == null || oldPoi.isDeleted()) {
            return false;
        }

        Poi newPoi = new Poi(oldPoi.getId(), newName, newLat, newLon, newDesc, newAddress, oldPoi.getCreatedTime(), LocalDateTime.now());

        try {
            this.poiData.insert(newPoi);
            oldPoi.delete();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Soft deletes POI from tree
     * sets internal attribute to deleted
     * @param poi to delete
     * @return true if delete was completed successfully otherwise false
     */
    public boolean deletePoi(Poi poi) {
        if (poi == null) {
            return false;
        }

        if (poi.isDeleted()) {
            return false;
        }

        poi.delete();
        return true;
    }

    /**
     * creates new tree according to Tree type and returns it
     * @param newTreeType
     * @return new tree structure
     */
    public IPoiData initTree(TreeType newTreeType) {
        IPoiData newTree;
        switch (newTreeType) {
            case KDTree -> {
                newTree = new KDTreeData();
            }
            case QuadTree -> {
                newTree = new QuadTreeData();
            }
            case QuadKDTree -> {
                newTree = new QuadKDTreeData();
            }
            default -> throw new IllegalArgumentException("Unknown tree type");
        }
        return newTree;
    }

    /**
     * Switches current tree to a new tree type
     * Loads data from old tree to new tree
     * @param newTreeType
     */
    public void switchTree(TreeType newTreeType) {
        IPoiData newTree = this.initTree(newTreeType);
        List<Poi> data = this.getAll();

        for (var poi : data) {
            newTree.insert(poi);
        }

        this.poiData = newTree;
    }

    /**
     * Writes data to CSV file
     * @param path to CSV file
     * @throws IOException
     */
    public void saveToCsv(String path) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("id;name;description;lat;lon;address;created_at;updated_at");
            writer.newLine();

            for (Poi poi : this.getAll()) {
                writer.write(poi.getId() + ";" +
                        poi.getName() + ";" +
                        poi.getDescription() + ";" +
                        poi.getLatitude() + ";" +
                        poi.getLongitude() + ";" +
                        poi.getAddress() + ";" +
                        poi.getCreatedTime() + ";" +
                        poi.getUpdatedTime());
                writer.newLine();
            }
        }
    }

    /**
     * Loads data from CSV and loads them into tree
     * @param path of loaded CSV
     * @throws IOException
     */
    public void loadFromCsv(String path) throws IOException {
        IPoiData newTree = initTree(this.currentTree);
        long newNextId = 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                Poi poi = getPoi(line);
                newTree.insert(poi);

                if (poi.getId() == Long.MAX_VALUE) {
                    throw new IllegalArgumentException("Ran out of available ID's");
                }

                if (poi.getId() >= newNextId) {
                    newNextId = poi.getId() + 1;
                }
            }
        }

        this.nextId = newNextId;
        this.poiData = newTree;
    }

    /**
     * Creates POI according to line from CSV
     * @param line from CSV
     * @return POI with values from CSV line
     * @throws IOException
     */
    private static Poi getPoi(String line) throws IOException {
        String[] values = line.split(";");

        return switch (values.length) {
            case 3 -> new Poi(
                        Long.parseLong(values[0]),
                        "",
                        Double.parseDouble(values[1]),
                        Double.parseDouble(values[2]),
                        "",
                        "",
                        LocalDateTime.now(),
                        LocalDateTime.now()
            );

            case 8 -> new Poi(
                    Long.parseLong(values[0]),
                    values[1],
                    Double.parseDouble(values[3]),
                    Double.parseDouble(values[4]),
                    values[2],
                    values[5],
                    LocalDateTime.parse(values[6]),
                    LocalDateTime.parse(values[7])
            );
            default -> throw new IllegalArgumentException("Incorrect number of columns: " + values.length);
        };
    }

    /**
     * Generates random POIs according to parameters
     * @param seed for generation
     * @param count number of points to be generated
     * @param minLat minimal latitude of possible POIs
     * @param maxLat maximal latitude of possible POIs
     * @param minLon minimal longitude of possible POIs
     * @param maxLon maximal longitude of possible POIs
     */
    public void generatePois(long seed, long count, double minLat, double maxLat, double minLon, double maxLon) {
        Random random = new Random(seed);

        for (long i = 0; i < count; i++) {
            double lat = random.nextDouble(minLat, maxLat);
            double lon = random.nextDouble(minLon, maxLon);

            boolean addedPoi = this.addPoi(
                    "", "", lat, lon, ""
            );
            if (!addedPoi)
                throw new IllegalArgumentException("Couldnt add POI");
        }
    }
}
