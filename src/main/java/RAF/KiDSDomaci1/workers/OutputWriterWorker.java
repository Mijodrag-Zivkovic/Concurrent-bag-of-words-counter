package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.DataForOutput;
import RAF.KiDSDomaci1.model.Output;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

public class OutputWriterWorker implements Runnable{

    private DataForOutput data;
    private Output output;

    public OutputWriterWorker(DataForOutput data, Output output) {
        this.data = data;
        this.output = output;
    }

    @Override
    public void run() {



        try {

            ConcurrentHashMap<String, Long> map = data.getWordOccurrences().get();
//            for (HashMap.Entry<String,Long> set :
//                    map.entrySet())
//            {
//                System.out.println(set.getKey() + " " + set.getValue());
//            }
            //output.getWordStorage().put(data.getFilename(), data.getWordOccurrences());
            int index = output.getListView().getItems().indexOf("*"+data.getFilename());
            if (index>-1)
            {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        output.getListView().getItems().set(index, data.getFilename());
                    }

                });
            }
            System.out.println("rezultat upisan");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //posalji signal guiju ? ili vrati nesto pa nek to uradi output worker
    }
}
