package tester;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * AI DOKUMENTACIA 3.3 
 */
public final class JTSQuadTreeAdapter<E> implements StructureAdapter<E> {
    private final Quadtree tree;
    private final Function<? super E, ? extends Envelope> envelopeOf;

    /**
     * AI DOKUMENTACIA 3.3 
     */
    public JTSQuadTreeAdapter(Quadtree tree,
                              Function<? super E, ? extends Envelope> envelopeOf) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
        this.envelopeOf = Objects.requireNonNull(envelopeOf, "Envelope function cannot be null");
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public void insert(E object) {
        Objects.requireNonNull(object, "Object cannot be null");
        Envelope envelope = Objects.requireNonNull(this.envelopeOf.apply(object), "Envelope cannot be null");
        this.tree.insert(envelope, object);
    }

    /**
     * AI DOKUMENTACIA 3.4
     */
    @Override
    @SuppressWarnings("unchecked") 
    public List<E> find(E min, E max) {
        Envelope lower = Objects.requireNonNull(this.envelopeOf.apply(
                Objects.requireNonNull(min, "Minimum boundaries cannot be null")), "Envelope cannot be null");
        Envelope upper = Objects.requireNonNull(this.envelopeOf.apply(
                Objects.requireNonNull(max, "Maximum boundaries cannot be null")), "Envelope cannot be null");
        if (lower.isNull() || upper.isNull()
                || lower.getMinX() > upper.getMaxX() || lower.getMinY() > upper.getMaxY()) {
            throw new IllegalArgumentException("Minimum boundaries cannot exceed maximum boundaries");
        }
        Envelope area = new Envelope(lower.getMinX(), upper.getMaxX(), lower.getMinY(), upper.getMaxY());
        List<E> result = new ArrayList<>();
        this.tree.query(area, object -> {
            E candidate = (E) object;
            if (area.intersects(this.envelopeOf.apply(candidate))) {
                result.add(candidate);
            }
        });
        return result;
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    @SuppressWarnings("unchecked") 
    public List<E> getAll() {
        return new ArrayList<>(this.tree.queryAll());
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public int size() {
        return this.tree.size();
    }
}
