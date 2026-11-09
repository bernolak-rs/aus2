package POIApp.model.trees;

import POIApp.model.Poi;

import java.util.List;

/**
 * Adapter interface for Tree data structures
 */
public interface IPoiData {
    /**
     * Inserts POI into a tree structure
     * @param poi
     */
    void insert(Poi poi);

    /**
     * Finds POIs in tree structure
     * @param minLat minimal latitude of bounds
     * @param maxLat maximal latitude of bounds
     * @param minLon minimal longitude of bounds
     * @param maxLon maximal longitude of bounds
     * @return POIs that belong to area
     */
    List<Poi> find(double minLat, double maxLat, double minLon, double maxLon);
}
