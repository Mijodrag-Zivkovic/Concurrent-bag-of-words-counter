package RAF.KiDSDomaci1.model;

import javafx.scene.chart.PieChart;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Cruncher {
	
	private int arity;
	private String name;
	private static AtomicInteger nameCounter = new AtomicInteger(0);
	private LinkedBlockingQueue<DataForCruncher> queue;
	private Output output;
	public Cruncher(int arity, Output output) {
		this.arity = arity;
		Integer i = nameCounter.getAndIncrement();
		this.name = "Cruncher " + i.toString();
		queue = new LinkedBlockingQueue<>();
		//this.name = "Counter 0";
		this.output = output;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public int getArity() {
		return arity;
	}

	public LinkedBlockingQueue<DataForCruncher> getQueue() {
		return queue;
	}

	public Output getOutput() {
		return output;
	}
}
