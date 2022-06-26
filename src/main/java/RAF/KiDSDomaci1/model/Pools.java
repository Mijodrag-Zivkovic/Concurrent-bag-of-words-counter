package RAF.KiDSDomaci1.model;

import RAF.KiDSDomaci1.workers.CruncherWorker;
import RAF.KiDSDomaci1.workers.FileInputWorker;
import RAF.KiDSDomaci1.workers.OutputWorker;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pools {

   public static ExecutorService fileInputPool = Executors.newCachedThreadPool();
   public static ExecutorService cruncherPool = Executors.newCachedThreadPool();
   public static ExecutorService outputPool = Executors.newCachedThreadPool();
   public static OutputWorker outputWorker = null;
   public static ArrayList<FileInputWorker> fileInputWorkers = new ArrayList<>();
   public static ArrayList<CruncherWorker> cruncherWorkers = new ArrayList<>();
   public static ArrayList<OutputWorker> outputWorkers = new ArrayList<>();

}
