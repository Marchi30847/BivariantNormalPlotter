package deep.data;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface PlotterContract {
    interface View {
        void setComputeListener(EventHandler<ActionEvent> eventHandler);
    }

    interface Presenter {
        void initListeners();
    }

    interface Model {

    }
}
