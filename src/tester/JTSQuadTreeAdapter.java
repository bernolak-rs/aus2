package tester;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/** The wrapped JTS tree must contain only objects of type E. */
public final class JTSQuadTreeAdapter<E> implements StructureAdapter<E> {
    private final Quadtree tree;
    private final Function<? super E, ? extends Envelope> envelopeOf;

    public JTSQuadTreeAdapter(Quadtree tree,
                              Function<? super E, ? extends Envelope> envelopeOf) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
        this.envelopeOf = Objects.requireNonNull(envelopeOf, "Envelope function cannot be null");
    }

    @Override
    public void insert(E object) {
        Objects.requireNonNull(object, "Object cannot be null");
        Envelope envelope = Objects.requireNonNull(this.envelopeOf.apply(object), "Envelope cannot be null");
        this.tree.insert(envelope, object);
    }

    @Override
    @SuppressWarnings("unchecked") // JTS exposes an untyped list of the stored objects.
    public List<E> getAll() {
        return new ArrayList<>(this.tree.queryAll());
    }

    @Override
    public int size() {
        return this.tree.size();
    }
}
