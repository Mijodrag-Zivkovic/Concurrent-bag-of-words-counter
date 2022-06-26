package RAF.KiDSDomaci1.model;

import RAF.KiDSDomaci1.view.MainView;

import java.util.concurrent.atomic.AtomicInteger;

public class FileInput {
	private Disk disk;
	private String name;
	private static AtomicInteger nameCounter = new AtomicInteger(0);
	volatile boolean running;
	private MainView mainView;
	
	public FileInput(Disk disk, MainView mainView) {
		Integer i = nameCounter.getAndIncrement();
		this.name = i.toString();
		//this.name = "0";
		this.disk = disk;
		running = true;
		this.mainView = mainView;
		//System.out.println(this.disk.toString());
	}
	
	public Disk getDisk() {
		return disk;
	}
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public MainView getMainView() {
		return mainView;
	}
}
