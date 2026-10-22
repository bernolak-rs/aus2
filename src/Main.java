import java.util.List;


import org.danilopianini.util.SpatialIndex;
import org.danilopianini.util.FlexibleQuadTree;

public class Main {
    public static void main(String[] args) {
        SpatialIndex<String> tree = new FlexibleQuadTree<>();

        tree.insert("A", 10, 20);
        tree.insert("B", 50, 60);
        tree.insert("C", 15, 25);

        List<String> result = tree.query(
            new double[]{0, 0},
            new double[]{30, 30}
        );

        System.out.println(result);
        //new TesterCli().start();
    }
}
