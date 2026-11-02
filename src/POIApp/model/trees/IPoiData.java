package POIApp.model.trees;

import POIApp.model.Poi;

import java.util.List;

public interface IPoiData {
    void insert(Poi poi);

    List<Poi> find(double minLat, double maxLat, double minLon, double maxLon);
}
