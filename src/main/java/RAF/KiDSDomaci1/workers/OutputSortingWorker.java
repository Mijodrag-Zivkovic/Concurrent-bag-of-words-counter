package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.Output;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class OutputSortingWorker implements Callable<HashMap<String,Long>> {
    private String filename;
    private Output output;
    private ProgressBar progressBar;
    private ConcurrentHashMap<String,Long> occurrenceMap;

    public OutputSortingWorker(String filename, Output output, ProgressBar progressBar) {
        this.filename = filename;
        this.output = output;
        this.progressBar = progressBar;
    }

    public OutputSortingWorker(ConcurrentHashMap<String, Long> occurrenceMap) {
        this.occurrenceMap = occurrenceMap;
    }


    @Override
    public HashMap<String, Long> call() throws Exception {
        HashMap<String, Long> sortedMap = null;
        try {
            ConcurrentHashMap<String,Long> map;
            if (output!=null)
            map=(ConcurrentHashMap<String, Long>) output.getWordStorage().get(filename).get();
            else
                map=this.occurrenceMap;
            int sort_progress_limit=10000;
            final double updateValue = ((double) map.size() * Math.log(map.size()) / (double) sort_progress_limit) / (double) 100;
            final int[] count = new int[1];
            //HashMap<String,Long> sortedMap = new HashMap<>();
            sortedMap = map
                    .entrySet()
                    .stream()
                    .sorted(Collections.reverseOrder((Map.Entry e1, Map.Entry e2) -> {
                        count[0]++;
                        if (count[0] % sort_progress_limit == 0) {
                            Platform.runLater(() -> progressBar.
                                    setProgress(progressBar.getProgress() + updateValue));
                        }
                        return Long.compare((Long)e1.getValue(),  (Long)e2.getValue());
                    }))
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Platform.runLater(()-> output.getMainView().getRight().getChildren().remove(progressBar));
        return sortedMap;
    }
}
