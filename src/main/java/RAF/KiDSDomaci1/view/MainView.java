package RAF.KiDSDomaci1.view;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import RAF.KiDSDomaci1.app.Config;
import RAF.KiDSDomaci1.model.*;
import RAF.KiDSDomaci1.workers.OutputWorker;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainView {
	private Stage stage;
	private ComboBox<Disk> disks;
	private HBox left;
	private VBox fileInput, cruncher;
	private Pane center, right;
	private ListView<String> results;
	private Button addFileInput, singleResult, sumResult;
	private ArrayList<FileInputView> fileInputViews;
	private LineChart<Number, Number> lineChart;
	private ArrayList<Cruncher> availableCrunchers;

	private Button addCruncher;

	private Output output;
	private OutputWorker outputWorker;


	public void initMainView(BorderPane borderPane, Stage stage) {

		this.stage = stage;

		fileInputViews = new ArrayList<FileInputView>();
		availableCrunchers = new ArrayList<Cruncher>();

		left = new HBox();

		borderPane.setLeft(left);

		initFileInput();

		initCruncher();

		initCenter(borderPane);

		initRight(borderPane);


		output = new Output(results,this);
		outputWorker = new OutputWorker(output);
		Pools.outputWorker = outputWorker;
//		Thread t = new Thread(outputWorker);
//		t.start();
		Pools.outputPool.submit(outputWorker);
	}

	private void initFileInput() {
		fileInput = new VBox();

		fileInput.getChildren().add(new Text("File inputs:"));
		VBox.setMargin(fileInput.getChildren().get(0), new Insets(0, 0, 10, 0));

		disks = new ComboBox<Disk>();
		disks.getSelectionModel().selectedItemProperty().addListener(e -> updateEnableAddFileInput());
		disks.setMinWidth(120);
		disks.setMaxWidth(120);
		fileInput.getChildren().add(disks);

		addFileInput = new Button("Add FileInput");
		addFileInput.setOnAction(e -> addFileInput(new FileInput(disks.getSelectionModel().getSelectedItem(),this)));
		VBox.setMargin(addFileInput, new Insets(5, 0, 10, 0));
		addFileInput.setMinWidth(120);
		addFileInput.setMaxWidth(120);
		fileInput.getChildren().add(addFileInput);

		int width = 210;

		VBox divider = new VBox();
		divider.getStyleClass().add("divider");
		divider.setMinWidth(width);
		divider.setMaxWidth(width);
		fileInput.getChildren().add(divider);
		VBox.setMargin(divider, new Insets(0, 0, 15, 0));

		Insets insets = new Insets(10);
		ScrollPane scrollPane = new ScrollPane(fileInput);
		scrollPane.setMinWidth(width + 35);
		fileInput.setPadding(insets);
		fileInput.getChildren().add(scrollPane);

		left.getChildren().add(scrollPane);

		
		try {
			String[] disksArray = Config.getProperty("disks").split(";");
			for (String disk : disksArray) {
				File file = new File(disk);
				if(!file.exists() || !file.isDirectory()) {
					throw new Exception("Bad directory path");
				}
				disks.getItems().add(new Disk(file));
			}
			if (disksArray.length > 0) {
				disks.getSelectionModel().select(0);
			}
		} catch (Exception e) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Closing");
					alert.setHeaderText("Bad config disks");
					alert.setContentText(null);

					alert.showAndWait();
					System.exit(0);
				}
			});
		}

		updateEnableAddFileInput();
	}

	private void initCruncher() {
		cruncher = new VBox();

		Text text = new Text("Crunchers");
		cruncher.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 5, 0));

		addCruncher = new Button("Add cruncher");
		addCruncher.setOnAction(e -> addCruncher());
		cruncher.getChildren().add(addCruncher);
		VBox.setMargin(addCruncher, new Insets(0, 0, 15, 0));

		int width = 110;

		Insets insets = new Insets(10);
		ScrollPane scrollPane = new ScrollPane(cruncher);
		scrollPane.setMinWidth(width + 35);
		cruncher.setPadding(insets);
		left.getChildren().add(scrollPane);
	}

	private void initCenter(BorderPane borderPane) {
		center = new HBox();

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Bag of words");
		yAxis.setLabel("Frequency");
		lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setMinWidth(700);
		lineChart.setMinHeight(600);
		center.getChildren().add(lineChart);

		borderPane.setCenter(center);
	}

	private void initRight(BorderPane borderPane) {
		right = new VBox();
		right.setPadding(new Insets(10));
		right.setMaxWidth(200);

		results = new ListView<String>();
		right.getChildren().add(results);
		VBox.setMargin(results, new Insets(0, 0, 10, 0));
		results.getSelectionModel().selectedItemProperty().addListener(e -> updateResultButtons());
		results.getSelectionModel().selectedIndexProperty().addListener(e -> updateResultButtons());
		results.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		singleResult = new Button("Single result");
		singleResult.setOnAction(e -> getSingleResult());
		singleResult.setDisable(true);
		right.getChildren().add(singleResult);
		VBox.setMargin(singleResult, new Insets(0, 0, 5, 0));

		sumResult = new Button("Sum results");
		sumResult.setDisable(true);
		sumResult.setOnAction(e -> sumResults());
		right.getChildren().add(sumResult);
		VBox.setMargin(sumResult, new Insets(0, 0, 10, 0));

		borderPane.setRight(right);
	}

	public void updateEnableAddFileInput() {
		Disk disk = disks.getSelectionModel().getSelectedItem();
		if (disk != null) {
			for (FileInputView fileInputView : fileInputViews) {
				if (fileInputView.getFileInput().getDisk() == disk) {
					addFileInput.setDisable(true);
					return;
				}
			}
			addFileInput.setDisable(false);
		} else {
			addFileInput.setDisable(true);
		}
	}

	public void updateResultButtons() {
		if (results.getSelectionModel().getSelectedItems() == null
				|| results.getSelectionModel().getSelectedItems().size() == 0) {
			singleResult.setDisable(true);
			sumResult.setDisable(true);
		} else if (results.getSelectionModel().getSelectedItems().size() == 1) {
			singleResult.setDisable(false);
			sumResult.setDisable(true);
		} else {
			singleResult.setDisable(true);
			sumResult.setDisable(false);
		}
	}

	private void getSingleResult() {
		ProgressBar progressBar = new ProgressBar();
		right.getChildren().add(progressBar);
		String filename = results.getSelectionModel().getSelectedItem();
		System.out.println(filename);
		if (filename.startsWith("*"))
			filename=filename.substring(1);
		if(output.getWordStorage().get(filename).isDone())
		{

			//ConcurrentHashMap<String,Long> map = output.getWordStorage().get(filename).get();
			HashMap<String, Long> map = outputWorker.getSingleResult(filename,progressBar);
			System.out.println("done");
			//sortiranje mape
			// pisanje u grid
			lineChart.getData().clear();
			XYChart.Series series = new XYChart.Series();
			series.setName("Top hundred word occurrences for " + filename);
			int i = 1;
			for (HashMap.Entry<String, Long> set :
					map.entrySet()) {
				if (i > 100)
					break;
				series.getData().add(new XYChart.Data(i, set.getValue()));
				i++;
				//System.out.println(set.getKey() + " " + set.getValue());
			}
			//series.getData().add(new XYChart.Data(1, 15));
			lineChart.getData().add(series);


		}
		else
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Error");
			alert.setHeaderText("File is still being processed!");
			alert.setContentText(null);
			alert.showAndWait();
			return;
		}
	}

	private void sumResults() {
		//ArrayList<String> files = new ArrayList<>();
		ProgressBar progressBar = new ProgressBar();
		right.getChildren().add(progressBar);
		outputWorker.getSummedResult(results.getSelectionModel().getSelectedItems(),progressBar);
	}

	public void addFileInput(FileInput fileInput) {
		FileInputView fileInputView = new FileInputView(fileInput, this);
		this.fileInput.getChildren().add(fileInputView.getFileInputView());
		VBox.setMargin(fileInputView.getFileInputView(), new Insets(0, 0, 30, 0));
		fileInputView.getFileInputView().getStyleClass().add("file-input");
		fileInputViews.add(fileInputView);
		if (availableCrunchers != null) {
			fileInputView.updateAvailableCrunchers(availableCrunchers);
		}
		updateEnableAddFileInput();
	}

	public void removeFileInputView(FileInputView fileInputView) {
		fileInput.getChildren().remove(fileInputView.getFileInputView());
		fileInputViews.remove(fileInputView);
		updateEnableAddFileInput();
	}

	public void updateCrunchers(ArrayList<Cruncher> crunchers) {
		for (FileInputView fileInputView : fileInputViews) {
			fileInputView.updateAvailableCrunchers(crunchers);
		}
		this.availableCrunchers = crunchers;
	}

	public Stage getStage() {
		return stage;
	}

	private void addCruncher() {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Add cruncher");
		dialog.setHeaderText("Enter cruncher arity");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(res -> {
			try {
				int arity = Integer.parseInt(res);
				for (Cruncher cruncher : availableCrunchers) {
					if (cruncher.getArity() == arity) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Error");
						alert.setHeaderText("Cruncher with this arity already exists.");
						alert.setContentText(null);
						alert.showAndWait();
						return;
					}
				}
				Cruncher cruncher = new Cruncher(arity,output);
				CruncherView cruncherView = new CruncherView(this, cruncher);
				this.cruncher.getChildren().add(cruncherView.getCruncherView());
				availableCrunchers.add(cruncher);
				updateCrunchers(availableCrunchers);
			} catch (NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Wrong input");
				alert.setHeaderText("Arity must be a number");
				alert.showAndWait();
			}
		});
	}

	public void stopCrunchers() {
		
	}

	public void stopFileInputs() {
		
	}

	public void removeCruncher(CruncherView cruncherView) {
		for (FileInputView fileInputView : fileInputViews) {
			fileInputView.removeLinkedCruncher(cruncherView.getCruncher());
		}
		availableCrunchers.remove(cruncherView.getCruncher());
		updateCrunchers(availableCrunchers);
		cruncher.getChildren().remove(cruncherView.getCruncherView());
	}

	public void terminate()
	{
		System.out.println("inside terminate");
		Alert alert =  new Alert(AlertType.INFORMATION, "Critical error! No memory left, nor right!", ButtonType.OK);
		alert.showAndWait();
//		new Thread(() -> {
//			Pools.fileInputPool.shutdownNow();
//			Pools.cruncherPool.shutdownNow();
//			Pools.outputPool.shutdownNow();
//		}).start();
		Pools.fileInputPool.shutdownNow();
		Pools.cruncherPool.shutdownNow();
		Pools.outputPool.shutdownNow();
		System.out.println("terminate 2");


		System.exit(1);
	}


	public Pane getRight() {
		return right;
	}

	public Output getOutput() {
		return output;
	}
}
