package POIApp.model.trees;

import POIApp.model.Poi;
import QuadTree.Bounds;
import QuadTree.CoordinateOps;
import QuadTree.SpatialAdapter;
import QuadTree.QuadKDTree;

import java.util.List;

import KDTree.KDComparator;

public class QuadKDTreeData implements IPoiData {
    private QuadKDTree<Poi, Double> tree;

    public QuadKDTreeData() {
        Bounds<Double> bounds = new Bounds<>(-180d, 180d, -90d, 90d);

        SpatialAdapter<Poi, Double> adapter = poi -> new Bounds<>(poi.getLongitude(), poi.getLongitude(), poi.getLatitude(), poi.getLatitude());

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

        KDComparator<Poi> comparator = (first, second, axis) ->
                        Double.compare(axis == 0 ? first.getLatitude() : first.getLongitude(),
                                axis == 0 ? second.getLatitude() : second.getLongitude());

        this.tree = new QuadKDTree<Poi, Double>(
                bounds,
                adapter,
                ops,
                30,
                comparator
        );
    }

    @Override
    public void insert(Poi poi) {
        this.tree.insert(poi);
    }

    @Override
    public List<Poi> find(double minLat, double maxLat, double minLon, double maxLon) {
        return this.tree.find(new Bounds<Double>(minLat, maxLat, minLon, maxLon));
    }
}
