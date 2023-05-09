package program.model;

import program.shared.*;

import java.util.List;

import program.view.Theme;
import static program.model.Model.MOT;

public interface ModelContact {
    MapPoint addressSearch(String address);
    void planRoute(MapPoint from, MapPoint to);
    void clearRoute();
    List<MapRoadSegment> getPlannedRoute();
    Iterable<String> getInstructions();
    Iterable<String> getPOIs();
    MapElement nearestNeighbor (MapPoint q);
    void setPOI(String id, String address);

    Address checkPOIRegistry(String id);
    void setTheme(String theme);
    Theme getTheme();
    void setModeOfTransportation(MOT modeOfTransportation);
    List<MapElement> getElementsToDraw();
    void setDrawingArea(float[] p1, float[] p2, int zoomLevel);
    float getMinLon();
    float getMinLat();
    float getMaxLon();
    float getMaxLat();
    void setDebug(boolean debug);

    void loadNewFile(String fileName);

    MapPoint getMiddlePoint();
}
