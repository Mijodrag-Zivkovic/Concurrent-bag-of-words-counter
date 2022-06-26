package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.Pools;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class CruncherCollectorWorker implements Callable<ConcurrentHashMap<String,Long>> {

    private ConcurrentHashMap<String,Long> wordOccurrences;
    private String text;
    private int arrity;
    private Text status;
    private String filename;

    public CruncherCollectorWorker(String text, int arrity, Text status,String filename) {
        this.wordOccurrences = new ConcurrentHashMap<>();
        this.arrity = arrity;
        this.text = text;
        this.status = status;
        this.filename = filename;
    }

    @Override
    public ConcurrentHashMap<String,Long> call() throws Exception {

        int textLength = text.length();
        //System.out.println(splitText.length);//21054766
        int limit = 10000000;
        ArrayList<Future<Integer>> jobs = new ArrayList<>();
        for (int i=0;i<textLength;i+=limit)
        {
            jobs.add(Pools.cruncherPool.submit(new CruncherWordCounterWorker(text,wordOccurrences,limit,i,arrity)));
        }
        //System.out.println("jobs size "+jobs.size());
        while(true)
        {
            int brojac = 0;
            //System.out.println("job size: " + jobs.size());
            for (Future<Integer> job : jobs)
            {
                if (job.isDone())
                {
                    brojac++;
                }
                else break;
            }
            if (brojac==jobs.size())
                break;
        }
        String name = filename.substring(filename.lastIndexOf('\\')+1);
        Platform.runLater(() -> status.setText(status.getText().replace("\n" + name, "")));
        //Platform.runLater(() -> status.setText(status.getText().replace("\n" + filename, "")));
        return wordOccurrences;
    }
}
