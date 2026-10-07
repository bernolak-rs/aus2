import java.util.LinkedList;


public class QuadTree<E extends Point2D> {
    private enum Quadrant {
        NW,
        NE,
        SE,
        SW
    }

    private class QTNode {
        private final double minX;
        private final double maxX;
        private final double minY;
        private final double maxY;
        private LinkedList<E> list;

        private QTNode nw;
        private QTNode ne;
        private QTNode se; 
        private QTNode sw;
        
        public QTNode(double minX, double maxX, double minY, double maxY) {
            this.list = new LinkedList<>();

            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;

            this.nw = null;
            this.ne = null;
            this.se = null;
            this.sw = null;
        }

        private double centerX() {
            return minX + (maxX - minX) / 2;
        }

        private double centerY() {
            return minY + (maxY - minY) / 2;
        }

        private Quadrant getQuadrant(double x, double y) {
            boolean east = (this.centerX() <= x);
            boolean north = (this.centerY() <= y);

            if (north && east) 
                return Quadrant.NE;
            
            if (north) 
                return Quadrant.NW;
            
            if (!east)
                return Quadrant.SW;
            
            return Quadrant.SE;
        }

        private QTNode getChild(Quadrant q) throws IllegalArgumentException {
            switch (q) {
                case NW:
                    return this.nw;
                case NE:
                    return this.ne;
                case SE:
                    return this.se;
                case SW:
                    return this.sw;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private void setChild(Quadrant q, QTNode child) throws IllegalArgumentException {
            switch (q) {
                case NW:
                    this.nw = child;
                    break;
                case NE:
                    this.ne = child;
                    break;
                case SE:
                    this.sw = child;
                    break;
                case SW:
                    this.sw = child;
                    break;
                default:
                    throw new IllegalArgumentException();
            } 
        }

        private QTNode createChild(Quadrant q) throws IllegalArgumentException {
            double childX = centerX();
            double childY = centerY();

            switch (q) {
                case NW:
                    return new QTNode(minX, childX, childY, maxY);
                case NE:
                    return new QTNode(childX, maxX, childY, maxY); 
                case SE:
                    return new QTNode(childX, maxX, minY, childY); 
                case SW:
                    return new QTNode(minX, childX, minY, childY);
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final int maxDepth;
    private QTNode root;

    public QuadTree(double minX, double maxX, double minY, double maxY, int maxDepth) {
        if (minX >= maxX || minY >= maxY) {
            throw new IllegalArgumentException("Minimum boundaries must be smaller than maximum boundaries.");
        }
        if (maxDepth < 0) {
            throw new IllegalArgumentException("Max depth can't be negative");
        }

        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.maxDepth = maxDepth;
        this.root = new QTNode(minX, maxX, minY, maxY);
    }

    public QuadTree(double minX, double maxX, double minY, double maxY) {
        this(minX, maxX, minY, maxY, 32);
    }

    public void insert(E data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        double x = data.getX();
        double y = data.getY();

        if (x < minX || x > maxX || y < minY || y > maxY) {
            throw new IllegalArgumentException("Point is outside of QuadTree bounds.");
        }

        QTNode current = root;
        int depth = 0;

        while (depth < maxDepth) {
            Quadrant q = current.getQuadrant(x, y);

            QTNode child = current.getChild(q);

            if (child == null) {
                child = current.createChild(q);
                current.setChild(q, child);
                child.list.add(data);
                return;
            }
            current = child;
            depth++;
        }

        current.list.add(data);
    }

    public LinkedList<E> find(double minX, double maxX, double minY, double maxY) {
        return new LinkedList<>();
    }    
}
