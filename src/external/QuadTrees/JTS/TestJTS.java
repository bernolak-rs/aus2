package external.QuadTrees.JTS;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;

import java.util.List;

public class TestJTS {
    public static void main(String[] args) {

        Quadtree tree = new Quadtree();

        tree.insert(
            new Envelope(10, 20, 10, 20),
            "A"
        );

        tree.insert(
            new Envelope(40, 50, 40, 50),
            "B"
        );

        tree.insert(
            new Envelope(15, 25, 15, 25),
            "C"
        );

        Envelope search = new Envelope(
            0, 30,
            0, 30
        );

        List<?> result = tree.query(search);

        System.out.println(result);
    }
}