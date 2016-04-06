package descriptorApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import descriptorApp.model.Description;
import descriptorApp.model.IOOperations;
import descriptorApp.view.DataBaseConfigDialogController;
import descriptorApp.view.DescriptionEditDialogController;
import descriptorApp.view.DescriptionNewDialogController;
import descriptorApp.view.DescriptionOverviewController;
import descriptorApp.view.RootLayoutController;

public class MainApp extends Application {

	// TODO add a button in "addDataBase" dialogue that loads data from current
	// dataBase
	// TODO New Description has this bug: tableNames and columns are retrieved
	// from a wrong HashMap. Maybe you need to have another one to refuse many
	// connections to DB
	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Description> descriptionData = FXCollections
			.observableArrayList();

	private HashMap<String, ArrayList<String>> tablesAndColumns;

	private IOOperations ioOperations;
	private DescriptionOverviewController descriptionOverviewController;
	private RootLayoutController rootLayoutController;

	public MainApp() {
		ioOperations = new IOOperations(this);
		tablesAndColumns = new HashMap<String, ArrayList<String>>();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("DescriptionApp");

		this.primaryStage.getIcons().add(
				new Image("file:resources/images/icon.png"));

		initRootLayout();

		showDescriptionOverview();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			// Give the controller access to the main app.
			rootLayoutController = loader.getController();
			rootLayoutController.setMainApp(this);

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the description overview inside the root layout.
	 */
	public void showDescriptionOverview() {
		try {
			// Load description overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/DescriptionOverview.fxml"));
			AnchorPane descriptionOverview = (AnchorPane) loader.load();

			// Set description overview into the center of root layout.
			rootLayout.setCenter(descriptionOverview);

			// Give the controller access to the main app.
			descriptionOverviewController = loader.getController();
			descriptionOverviewController.setMainApp(this);

			rootLayoutController
					.setDescriptionOverviewController(descriptionOverviewController);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDescriptionOVerviewTableItems() {
		descriptionOverviewController.setTableItems();
	}

	public boolean showDescriptionEditDialog(Description description) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/DescriptionEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Description");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the description into the controller.
			DescriptionEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setDescription(description);

			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean showDescriptionNewDialog(Description description) {
		try {
			// Load the fxml file and create a new stage for the popup dialog.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/DescriptionNewDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("New Description");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the description into the controller.
			DescriptionNewDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setDescription(description);
			controller.getTableNameCombo()
					.setItems(
							FXCollections.observableArrayList(tablesAndColumns
									.keySet()));
			controller.setMainApp(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean showDataBaseConfigDialog() {
		try {
			// Load the fxml file and create a new stage for the popup dialog.

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/DataBaseConfigDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("New DataBase Connection");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Set the description into the controller.
			DataBaseConfigDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(this);
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();

			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void initialDescriptions() {
		tablesAndColumns.clear();
		descriptionData = FXCollections.observableArrayList();
		ioOperations.getInitialDescriptionsFromDB(tablesAndColumns);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public ObservableList<Description> getDescriptionData() {
		return descriptionData;
	}

	public void setDescriptionData(ObservableList<Description> descriptionData) {
		this.descriptionData = descriptionData;
	}

	public HashMap<String, ArrayList<String>> getTablesAndColumns() {
		return tablesAndColumns;
	}

	public void setTablesAndColumns(
			HashMap<String, ArrayList<String>> tablesAndColumns) {
		this.tablesAndColumns = tablesAndColumns;
	}

	public IOOperations getIoOperations() {
		return ioOperations;
	}

	public void setIoOperations(IOOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
