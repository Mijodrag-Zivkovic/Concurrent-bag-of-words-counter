package RAF.KiDSDomaci1.model;

import RAF.KiDSDomaci1.view.MainView;
import javafx.scene.control.ListView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class Output {
    private LinkedBlockingQueue<DataForOutput> input;
    private ConcurrentHashMap<String, Future<ConcurrentHashMap<String,Long>>> wordStorage;
    private ListView<String> listView;
    private MainView mainView;

    public Output(ListView<String> listView, MainView mainView) {
        input = new LinkedBlockingQueue<>();
        wordStorage = new ConcurrentHashMap<>();
        this.listView = listView;
        this.mainView = mainView;
    }

    public LinkedBlockingQueue<DataForOutput> getInput() {
        return input;
    }

    public ConcurrentHashMap<String, Future<ConcurrentHashMap<String, Long>>> getWordStorage() {
        return wordStorage;
    }

    public ListView<String> getListView() {
        return listView;
    }

    public MainView getMainView() {
        return mainView;
    }
}
