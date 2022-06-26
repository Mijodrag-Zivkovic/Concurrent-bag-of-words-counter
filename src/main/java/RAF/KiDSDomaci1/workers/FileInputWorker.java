package RAF.KiDSDomaci1.workers;

import RAF.KiDSDomaci1.model.DataForCruncher;
import RAF.KiDSDomaci1.model.Directory;
import RAF.KiDSDomaci1.model.FileInput;
import RAF.KiDSDomaci1.model.Pools;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

public class FileInputWorker implements Runnable {

    public  ConcurrentHashMap<String,Long> lastModifiedMap;
    private FileInput fileInput;
    private List<Directory> directories;
    private LinkedBlockingQueue<DataForCruncher> queue;
    private ConcurrentLinkedQueue<LinkedBlockingQueue<DataForCruncher>> queuesList;
    private boolean paused;
    Text status;

    public FileInputWorker(FileInput fileInput, List<Directory> directories, Text status) {
        this.fileInput = fileInput;
        this.directories = directories;
        this.lastModifiedMap = new ConcurrentHashMap<>();
        this.queue = new LinkedBlockingQueue<>();
        this.paused = false;
        queuesList = new ConcurrentLinkedQueue<>();
        this.status = status;
    }

    public void deleteDir(Directory directory){

        //printaj celu mapu
        System.out.println("delete dir ////////////////////////////////////");
        for (HashMap.Entry<String, Long> set :
                lastModifiedMap.entrySet())
        {
            System.out.println(set.getKey());
            System.out.println(directory.toString());
            if (set.getKey().contains(directory.toString()))
            {
                lastModifiedMap.remove(set.getKey());
            }
        }
        System.out.println("delete dir after /////////////////////////////////");
        for (HashMap.Entry<String, Long> set :
                lastModifiedMap.entrySet())
        {
            System.out.println(set.getKey());
        }
        //printaj opet celu mapu
    }

    public synchronized void pause(){
        try {
            System.out.println("pause");
            wait();
            System.out.println("resume");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void resume(){
        notifyAll();
    }

    private synchronized void coolDown()
    {
        try {
            wait(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        //System.out.println(fileInput.getName());
        while(fileInput.isRunning())
        {
            //System.out.println("ulazak " + fileInput.getName());
            for (Directory d : directories)
            {
                //System.out.println(d.toString());
                File dir = new File(d.toString());
                File[] directoryListing = dir.listFiles();
                for (File file : directoryListing)
                {
                    if(this.paused){
                        pause();
                    }
                    if(lastModifiedMap.containsKey(d.toString()+File.separator+file.getName()))
                    {
                        if (file.lastModified()>lastModifiedMap.get(d.toString()+File.separator+file.getName()))
                        {
                            //System.out.println("if");
                            //String content = readFile(d.toString()+File.separator+file.getName());
                            Pools.fileInputPool.submit(new FileReaderWorker(d.toString()+File.separator+file.getName(),file.getName(),queuesList,status,fileInput));
                            lastModifiedMap.put(d.toString()+File.separator+file.getName(),file.lastModified());
                        }
                    }
                    else
                    {
                        //System.out.println("else");
                        //String content = readFile(d.toString()+File.separator+file.getName());
                        Pools.fileInputPool.submit(new FileReaderWorker(d.toString()+File.separator+file.getName(),file.getName(),queuesList,status,fileInput));
                        lastModifiedMap.put(d.toString()+File.separator+file.getName(),file.lastModified());
                    }
                }
            }
            //System.out.println("before wait");
            coolDown();
            //System.out.println("after wait");
        }
        System.out.println("izlazak " + fileInput.getName());
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public ConcurrentLinkedQueue<LinkedBlockingQueue<DataForCruncher>> getQueuesList() {
        return queuesList;
    }

    public FileInput getFileInput() {
        return fileInput;
    }
}
