package domain;

import data.Controller;

public class Presenter implements PresenterContract{
    private Controller controller = new Controller(0, 0, 0, 0, 0);

    @Override
    public void updateGraph(double meanA, double meanB, double stdDevA, double stdDevB, double correlation) {
        controller = new Controller(meanA, meanB, stdDevA, stdDevB, correlation);
        controller.calculateGraphVariables().calculateDensity(0, 0);
    }

    @Override
    public double getNewDensity(double x, double y) {
        return controller.calculateDensity(x, y).getDensity();
    }
}
