package program.shared;

import java.io.Serializable;

public class Point implements Serializable {
    private final float x, y;
    private transient long id;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(float x, float y, long id) {
        this(x, y);
        this.id = id;
    }

    public long getId() { return id; }
    public float getX() { return x; }
    public float getY() { return y; }
}
