package program.shared;

/**
 * Represents any shape with more than two points
 */
public abstract class MapShape extends MapElement{

    protected float[] points;
    protected int count;

    MapShape(String type, int size){
        super(type);
        points = new float[size * 2];
        count = 0;
    }

    public void add(Point point) {
        if (point.getX() < minPoint[0]){
            minPoint[0] = point.getX();
        }
        if (point.getX() > maxPoint[0]) {
            maxPoint[0] = point.getX();
        }
        if (point.getY() < minPoint[1]) {
            minPoint[1] = point.getY();
        }
        if (point.getY() > maxPoint[1]) {
            maxPoint[1] = point.getY();
        }
        points[count] = point.getX();
        count++;
        points[count] = point.getY();
        count++;
    }
}
