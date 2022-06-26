package RAF.KiDSDomaci1.app;

import RAF.KiDSDomaci1.model.Cruncher;
import RAF.KiDSDomaci1.model.DataForCruncher;
import RAF.KiDSDomaci1.model.DataForOutput;
import RAF.KiDSDomaci1.model.Pools;
import RAF.KiDSDomaci1.view.MainView;
import RAF.KiDSDomaci1.workers.CruncherWorker;
import RAF.KiDSDomaci1.workers.FileInputWorker;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
    	BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 1200, 650);
		MainView mainView = new MainView();
		mainView.initMainView(root, stage);
		stage.setScene(scene);
		stage.show();
    }

	@Override
	public void stop() throws Exception {
		System.out.println("whatever");
		Pools.fileInputPool.shutdown();
		for (FileInputWorker fileInputWorker:Pools.fileInputWorkers)
			fileInputWorker.getFileInput().setRunning(false);


		for (CruncherWorker cruncherWorker : Pools.cruncherWorkers)
			cruncherWorker.getCruncher().getQueue().add(new DataForCruncher(true));
		Pools.cruncherPool.shutdown();

		Pools.outputPool.shutdown();
		Pools.outputWorker.getOutput().getInput().add(new DataForOutput(true));
		super.stop();
	}
}