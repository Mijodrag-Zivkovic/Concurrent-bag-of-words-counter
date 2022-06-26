package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.Output;
import RAF.KiDSDomaci1.model.Pools;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.scene.control.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class OutputSumWorker implements Callable<ConcurrentHashMap<String,Long>> {

    Output output;
    ObservableList<String> filenames;
    ProgressBar progressBar;

    public OutputSumWorker(Output output, ObservableList<String> filenames, ProgressBar progressBar) {
        this.output = output;
        this.filenames = filenames;
        this.progressBar = progressBar;
    }

    @Override
    public ConcurrentHashMap<String, Long> call() throws Exception {

        ConcurrentHashMap<String,Long> finalMap = new ConcurrentHashMap<>();
        ArrayList<Future<HashMap<String,Long>>> jobs = new ArrayList<>();
        ArrayList<Integer> jobsDone = new ArrayList<>();
        ArrayList<Future<Boolean>> mergingJobs = new ArrayList<>();
//        for (String filename : filenames)
//        {
//            ConcurrentHashMap<String,Long> map = output.getWordStorage().get(filename).get();
//            for (Map.Entry<String,Long> entry : map.entrySet())
//            {
//                System.out.println(entry.getKey()+" : "+entry.getValue());
//            }
//        }
        for (String filename : filenames)
        {
            jobs.add(Pools.outputPool.submit(new OutputSortingWorker(filename,output, progressBar)));
            jobsDone.add(0);
        }
        int brojac = jobs.size();
        System.out.println("u while-u");
        while(brojac>0)
        {

            //System.out.println("job size: " + jobs.size());
            for (Future<HashMap<String,Long>> job : jobs)
            {
                if (job.isDone() && jobsDone.get(jobs.indexOf(job))==0)
                {

                    HashMap<String,Long> map = job.get();
                    Future<Boolean> future = Pools.outputPool.submit(()-> {
                        map.forEach(
                                (key, value) -> finalMap.merge(key, value, (v1, v2) -> v1.longValue()+v2.longValue() ));
                        return true;
                    });
                    mergingJobs.add(future);
                    brojac--;
                    jobsDone.set(jobs.indexOf(job),1);
                }
                //else break;
            }

        }
        System.out.println("izasao iz while-a");

        while(true)
        {
            int counter = 0;
            for (Future<Boolean> job : mergingJobs)
            {
                if (job.isDone())
                    counter++;
                else
                    break;
            }
            if (counter==mergingJobs.size())
                break;
        }
//        for (Map.Entry<String,Long> entry : finalMap.entrySet())
//        {
//            System.out.println(entry.getKey()+" : "+entry.getValue());
//        }
        System.out.println("nakon fora");
        Future<HashMap<String,Long>> future = Pools.outputPool.submit(new OutputSortingWorker(finalMap));
        Future<ConcurrentHashMap<String,Long>> future2 = Pools.outputPool.submit(()->{
            return new ConcurrentHashMap<>(future.get());
        });
        output.getWordStorage().put("custom",future2);
        Platform.runLater(()->output.getListView().getItems().add("custom"));

        //HashMap<String,Long> finalMap = new HashMap<>();

        //ubacivanje u mapu
        //ubacivanje u storage
        //sortiranje i vracanje


        return finalMap;
    }
}
