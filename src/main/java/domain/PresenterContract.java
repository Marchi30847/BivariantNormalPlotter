package domain;

public interface PresenterContract {
    void updateGraph(double meanA, double meanB, double stdDevA, double stdDevB, double correlation);
    double getNewDensity(double x, double y);
}
