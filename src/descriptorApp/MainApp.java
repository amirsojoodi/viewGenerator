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
import descriptorApp.model.DBConnection;
import descriptorApp.model.Description;
import descriptorApp.model.IOOperations;
import descriptorApp.view.DataBaseConfigDialogController;
import descriptorApp.view.DescriptionEditDialogController;
import descriptorApp.view.DescriptionNewDialogController;
import descriptorApp.view.DescriptionOverviewController;
import descriptorApp.view.RootLayoutController;
import descriptorApp.view.ViewCreatorDialogController;

public class MainApp extends Application {

	// TODO add a button in "addDataBase" dialogue that loads data from current
	// dataBase
	// TODO connectionsFile should be hidden and not accessible by others
	// TODO in connections when handling new, accept the not used before names
	// TODO when creating new connection, a new file have to be assigned to the
	// connection view [when changing the name of a connection the name of its
	// related file have to be changed too
	// TODO implement and add button for search
	// TODO add delete table functionality in description page
	// TODO preventing to set name "Views" and "Connections" for corresponding treeView(s)
	// TODO preview
	// TODO Execute Query

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Description> descriptionData = FXCollections
			.observableArrayList();
	private ObservableList<DBConnection> connectionData = FXCollections
			.observableArrayList();

	private HashMap<String, ArrayList<String>> tablesAndColumns;
	private HashMap<String, ArrayList<String>> allTablesAndColumnsFromDB;

	private IOOperations ioOperations;
	private DescriptionOverviewController descriptionOverviewController;
	private RootLayoutController rootLayoutController;
	
	private DBConnection activeConnection;

	public MainApp() {
		ioOperations = new IOOperations(this);
		tablesAndColumns = new HashMap<String, ArrayList<String>>();
		allTablesAndColumnsFromDB = new HashMap<String, ArrayList<String>>();
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
			if (allTablesAndColumnsFromDB.size() == 0) {
				ioOperations.loadAllColumnsFromDB(allTablesAndColumnsFromDB,
						false);
			}
			controller.getTableNameCombo().setItems(
					FXCollections.observableArrayList(allTablesAndColumnsFromDB
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

	public boolean showViewCreatorDialog() {
		try {
			// Load description overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class
					.getResource("view/ViewCreatorDialog.fxml"));
			AnchorPane pane = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("View Creator");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(pane);
			dialogStage.setScene(scene);

			ViewCreatorDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setMainApp(this);
			// controller.initialTablesTreeView();
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
		allTablesAndColumnsFromDB.clear();
		descriptionData = FXCollections.observableArrayList();
		ioOperations.getInitialDescriptionsFromDB(tablesAndColumns);
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public ObservableList<DBConnection> getConnectionData() {
		return connectionData;
	}

	public void setConnectionData(ObservableList<DBConnection> connectionData) {
		this.connectionData = connectionData;
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

	public HashMap<String, ArrayList<String>> getAllTablesAndColumnsFromDB() {
		return allTablesAndColumnsFromDB;
	}

	public void setAllTablesAndColumnsFromDB(
			HashMap<String, ArrayList<String>> allTablesAndColumnsFromDB) {
		this.allTablesAndColumnsFromDB = allTablesAndColumnsFromDB;
	}

	public IOOperations getIoOperations() {
		return ioOperations;
	}

	public void setIoOperations(IOOperations ioOperations) {
		this.ioOperations = ioOperations;
	}

	public DBConnection getActiveConnection() {
		return activeConnection;
	}

	public void setActiveConnection(DBConnection activeConnection) {
		this.activeConnection = activeConnection;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
