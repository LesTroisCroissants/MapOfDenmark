package program.model;

import program.shared.MapElement;
import java.util.List;

import program.shared.MapPoint;
import program.shared.MapRoadSegment;
import program.shared.Point;
import program.view.Theme;
import static program.model.Model.MOT;

public interface ModelContact {
    public MapPoint addressSearch(String address);
    public void planRoute(MapPoint from, MapPoint to);
    public List<MapRoadSegment> getPlannedRoute();
    public Iterable<String> getInstructions();
    public MapElement nearestNeighbor (MapPoint q);
    public void setPOI(String id, String address);
    public void setPOI(String id, float lat, float lon);
    public void setTheme(String theme);
    public Theme getTheme();
    public void setModeOfTransportation(MOT modeOfTransportation);
    public List<MapElement> getElementsToDraw();
    public void setDrawingArea(float[] p1, float[] p2, int zoomLevel);
    public float getMinLon();
    public float getMinLat();
    public float getMaxLon();
    public float getMaxLat();
    public void setDebug(boolean debug);

    void loadNewFile(String fileName);
}
