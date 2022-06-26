package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.DataForCruncher;
import RAF.KiDSDomaci1.model.FileInput;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileReaderWorker implements Runnable{

    private String path;
    private ConcurrentLinkedQueue<LinkedBlockingQueue<DataForCruncher>> queuesList;
    private String filename;
    private Text status;
    private FileInput fileInput;

    public FileReaderWorker(String path, String filename, ConcurrentLinkedQueue<LinkedBlockingQueue<DataForCruncher>> queuesList,
                            Text status, FileInput fileInput) {
        this.path = path;
        this.queuesList = queuesList;
        this.filename = filename;
        this.status = status;
        this.fileInput = fileInput;
    }

    @Override
    public void run() {

        synchronized (fileInput.getDisk())
        {
            Platform.runLater(() -> status.setText("Reading: " + filename));
            String content=null;
            String[] splitText = null;
            ArrayList<String> words = new ArrayList<>();
            try {
                //System.out.println(path);
                //content = Files.readString(Paths.get(path));
                File file = new File(path);
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fileInputStream.read(data);
                content = new String(data, StandardCharsets.US_ASCII);
//                InputData inputData = new InputData(file.getName(), content);
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }catch(OutOfMemoryError e){
                System.out.println("out of mem fi");
                Platform.runLater(()->fileInput.getMainView().terminate());
            }

            Platform.runLater(() -> status.setText("Idle"));
            for (LinkedBlockingQueue<DataForCruncher> queue : queuesList)
            {
                queue.add(new DataForCruncher(path,content));
            }

        }
    }

}
