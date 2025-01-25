package deep.presentation;

import deep.data.PlotterContract;

public class PlotterPresenter implements PlotterContract.Presenter {
    private PlotterContract.View view;
    private PlotterContract.Model model;

    public PlotterPresenter(PlotterContract.View view, PlotterContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void initListeners() {
        view.setComputeListener(_ -> {

        });
    }
}
