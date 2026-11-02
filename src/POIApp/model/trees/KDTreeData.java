package POIApp.model.trees;

import KDTree.KDTree;
import POIApp.model.Poi;

import java.util.List;

public class KDTreeData implements IPoiData {
    private KDTree<Poi> tree;

    public KDTreeData() {
        this.tree = new KDTree<Poi>(2,
                (first, second, axis) ->
                Double.compare(
                        axis == 0 ? first.getLatitude() : first.getLongitude(),
                        axis == 0 ? second.getLatitude() : second.getLongitude()
                ));
    }

    @Override
    public void insert(Poi poi) {
        this.tree.insert(poi);
    }

    @Override
    public List<Poi> find(double minLat, double maxLat, double minLon, double maxLon) {
         return tree.find(new Poi(-1, "", minLat, minLon, "", null, null, null),
                new Poi(-1, "", maxLat, maxLon, "", null, null, null));
    }
}
