package program.view;

public interface ViewContact {
    void showHelpPopup();
    void showInfoPopup();
    void showDirectionsPopup(Iterable<String> directions);
    void showPOIListPopup(Iterable<String> poiList);
}
