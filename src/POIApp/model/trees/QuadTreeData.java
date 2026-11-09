package POIApp.model.trees;

import POIApp.model.Poi;
import QuadTree.QuadTree;
import QuadTree.Bounds;
import QuadTree.CoordinateOps;
import QuadTree.SpatialAdapter;

import java.util.List;

/**
 * POI data adapter implementation for Quad tree
 */
public class QuadTreeData implements IPoiData {
    private QuadTree<Poi, Double> tree;

    public QuadTreeData() {
        Bounds<Double> bounds = new Bounds<>(-180d, 180d, -90d, 90d);

        SpatialAdapter<Poi, Double> adapter = poi -> new Bounds<>(poi.getLatitude(), poi.getLatitude(), poi.getLongitude(), poi.getLongitude());

        CoordinateOps<Double> ops = new CoordinateOps<Double>() {
            @Override
            public int compare(Double first, Double second) {
                return Double.compare(first, second);
            }

            @Override
            public Double midpoint(Double min, Double max) {
                return min + (max - min) / 2.0;
            }
        };

        this.tree = new QuadTree<Poi, Double>(
                bounds,
                adapter,
                ops,
                30
        );
    }

    @Override
    public void insert(Poi poi) {
        this.tree.insert(poi);
    }

    @Override
    public List<Poi> find(double minLat, double maxLat, double minLon, double maxLon) {
        return this.tree.find(new Bounds<Double>(minLat, maxLat, minLon, maxLon)).stream().filter(poi -> !poi.isDeleted()).toList();
    }
}
