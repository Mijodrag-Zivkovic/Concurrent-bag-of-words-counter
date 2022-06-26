package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.Cruncher;
import RAF.KiDSDomaci1.model.DataForCruncher;
import RAF.KiDSDomaci1.model.DataForOutput;
import RAF.KiDSDomaci1.model.Pools;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class CruncherWorker implements Runnable{

    private Cruncher cruncher;
    private Text status;


    public CruncherWorker(Cruncher cruncher, Text status) {
        this.cruncher = cruncher;
        this.status = status;
    }

    @Override
    public void run() {
        //System.out.println("pokrenut cruncher " + cruncher.toString());
        while(true){
            try
            {
                //System.out.println("before take");
                DataForCruncher data = cruncher.getQueue().take();
                //System.out.println("after take");
                //System.out.println(data.getText());
                if (data.isPoison()){
                    break;
                }
                else
                {
                    String text = data.getText();
                    //System.out.println(data.getFilename());
                    String name = data.getFilename().substring(data.getFilename().lastIndexOf('\\')+1);
                    //System.out.println(name);
                    Platform.runLater(() -> status.setText(status.getText() + "\n" + name));
                    //Platform.runLater(() -> status.setText(status.getText() + "\n" + data.getFilename()));
                    //System.out.println("duzina " + text.length());
//                    for (int i = 0; i < text.length();i++)
//                    {
//                        if (Character.isWhitespace(text.charAt(i)))
//                        {
//                            System.out.println(i);
//                        }
//                        else
//                            System.out.println("a");
//                    }
                    //String splitText[] = text.split("\\W+");
                    Future<ConcurrentHashMap<String,Long>> wordOccurrences = Pools.cruncherPool.submit(new CruncherCollectorWorker(text, cruncher.getArity(),status,data.getFilename()));
                    cruncher.getOutput().getInput().add(new DataForOutput(wordOccurrences, data.getFilename()+"-arity"+cruncher.getArity()));
                    System.gc();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        System.out.println("gasenje crunchera " + cruncher.toString());
    }

    public Cruncher getCruncher() {
        return cruncher;
    }
}
