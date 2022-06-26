package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.DataForOutput;
import RAF.KiDSDomaci1.model.Output;
import RAF.KiDSDomaci1.model.Pools;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class OutputWorker implements Runnable{

    private Output output;

    public OutputWorker(Output output) {
        this.output = output;
    }

    @Override
    public void run() {
        while(true)
        {
            try {
                DataForOutput data = output.getInput().take();
                if(data.isPoison())
                    break;
                System.out.println("output " + data.getFilename());
                //getuj filename i salji na gui
                //prosledi jobove
                //output.getListView().getItems().add("*"+data.getFilename());
                output.getWordStorage().put(data.getFilename(), data.getWordOccurrences());
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        output.getListView().getItems().add("*"+data.getFilename());
                    }

                });
                Pools.outputPool.submit(new OutputWriterWorker(data, output));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String,Long> getSingleResult(String fileName, ProgressBar progressBar)
    {
        Future<HashMap<String,Long>> futureSortedMap = Pools.outputPool.submit(new OutputSortingWorker(fileName,output,progressBar));
            HashMap<String,Long> sortedMap = null;
        try {
             sortedMap = futureSortedMap.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return sortedMap;
    }

    public ConcurrentHashMap<String,Long> getSummedResult(ObservableList<String> filenames,ProgressBar progressBar)
    {
        Future<ConcurrentHashMap<String,Long>> futureMap = Pools.outputPool.submit(new OutputSumWorker(output,filenames,progressBar));
        ConcurrentHashMap<String,Long> sortedMap = null;
        try {
            sortedMap = futureMap.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return sortedMap;
    }

    public Output getOutput() {
        return output;
    }
}
