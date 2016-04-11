package descriptorApp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.Dialogs;

import descriptorApp.MainApp;
import descriptorApp.model.Description;
import descriptorApp.model.IOOperations;

public class DescriptionOverviewController {

	@FXML
	private TableView<Description> descriptionTable;

	@FXML
	private TableColumn<Description, String> tableNameColumn;

	@FXML
	private TableColumn<Description, String> columnNameColumn;

	@FXML
	private TableColumn<Description, String> understandableNameColumn;

	@FXML
	private Label descriptionLabel;

	@FXML
	private Button discardChanges;

	@FXML
	private Button applyChanges;

	@FXML
	private Button loadAllColumns;

	private MainApp mainApp;

	public DescriptionOverviewController() {

	}

	@FXML
	private void initialize() {
		tableNameColumn.setCellValueFactory(cellData -> cellData.getValue()
				.tableNameProperty());
		columnNameColumn.setCellValueFactory(cellData -> cellData.getValue()
				.columnNameProperty());
		understandableNameColumn.setCellValueFactory(cellData -> cellData
				.getValue().understandableNameProperty());

		descriptionTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showDescriptionDetails(newValue));

		discardChanges.setDisable(true);
		applyChanges.setDisable(true);

		Description.hasAnyChanged
				.addListener((observable, oldValue, newValue) -> {
					if (newValue == true) {
						discardChanges.setDisable(false);
						applyChanges.setDisable(false);
					} else {
						discardChanges.setDisable(true);
						applyChanges.setDisable(true);
					}
				});
	}

	public void showDescriptionDetails(Description description) {
		if (description != null) {
			// Fill the labels with info from the description object.
			String text = description.getDescription();
			descriptionLabel.setText(text);

		} else {
			descriptionLabel.setText("");
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public void setTableItems() {
		// Add observable list data to the table
		descriptionTable.setItems(mainApp.getDescriptionData());
	}

	@FXML
	public void handleDeleteDescription() {
		int selectedIndex = descriptionTable.getSelectionModel()
				.getSelectedIndex();
		if (selectedIndex >= 0) {
			descriptionTable.getItems().get(selectedIndex)
					.addToDeletedDescriptions();
			descriptionTable.getItems().remove(selectedIndex);
			Description.hasAnyChanged.setValue(true);
		} else {
			// Nothing selected.
			Dialogs.create().title("No Selection")
					.masthead("No Description Selected")
					.message("Please select a description in the table.")
					.showWarning();
		}
	}

	@FXML
	public void handleNewDescription() {
		if (mainApp.getIoOperations().getDbName() == null) {
			Dialogs.create().title("No DataBase added!")
					.masthead("Please Configure a new DataBase")
					.message("By clicking on 'Add a DataBase' button.")
					.showWarning();
			return;
		}
		Description tempDescription = new Description();
		boolean okClicked = mainApp.showDescriptionNewDialog(tempDescription);
		if (okClicked) {
			mainApp.getDescriptionData().add(tempDescription);
		}
	}

	@FXML
	public void handleLoadAllColumns() {
		if (mainApp.getIoOperations().getDbName() == null) {
			Dialogs.create().title("No DataBase added!")
					.masthead("Please Configure a new DataBase")
					.message("By clicking on 'Add a DataBase' button.")
					.showWarning();
			return;
		}
		mainApp.getIoOperations().loadAllColumnsFromDB(mainApp.getTablesAndColumns(), true);
	}

	@FXML
	public void handleEditDescription() {
		Description selectedDescription = descriptionTable.getSelectionModel()
				.getSelectedItem();
		if (selectedDescription != null) {
			boolean okClicked = mainApp
					.showDescriptionEditDialog(selectedDescription);
			if (okClicked) {
				showDescriptionDetails(selectedDescription);
			}

		} else {
			// Nothing selected.
			Dialogs.create().title("No Selection")
					.masthead("No Description Selected")
					.message("Please select a description in the table.")
					.showWarning();
		}
	}

	@FXML
	public void handleAddDataBase() {
		if (Description.hasAnyChanged.getValue() == true) {
			Action dialogAction = Dialogs.create().title("Unsaved Data")
					.masthead("You have unsaved changes.")
					.message("Do you want to apply changes?").showConfirm();
			if (dialogAction == Actions.YES || dialogAction == Actions.OK) {
				handleApplyChanges();
			} else if (dialogAction == Actions.CLOSE
					|| dialogAction == Actions.CANCEL) {
				return;
			}
		}
		mainApp.getIoOperations().loadConnectionDataFromFile();
		boolean okClicked = mainApp.showDataBaseConfigDialog();

		if (okClicked) {
			mainApp.initialDescriptions();
			mainApp.setDescriptionOVerviewTableItems();
		}
	}
	
	@FXML
	public void handleViewCreator() {
		if (mainApp.getIoOperations().getDbName() == null) {
			Dialogs.create().title("No DataBase added!")
					.masthead("Please Configure a new DataBase")
					.message("By clicking on 'Add a DataBase' button.")
					.showWarning();
			return;
		}
		
		mainApp.getIoOperations().loadViewDataFromFile();
		boolean okClicked = mainApp.showViewCreatorDialog();
		
		if (okClicked) {
			mainApp.initialDescriptions();
			mainApp.setDescriptionOVerviewTableItems();
		}
	}

	@FXML
	public void handleAddDataBaseTest() {
		mainApp.getIoOperations().setServerIP("127.0.0.1");
		mainApp.getIoOperations().setServerPort("1433");
		mainApp.getIoOperations().setDbName("TestDB");
		mainApp.getIoOperations().setDbUsername("sa");
		mainApp.getIoOperations().setDbPassword("");

		mainApp.initialDescriptions();
		mainApp.setDescriptionOVerviewTableItems();
	}

	@FXML
	public void handleApplyChanges() {

		if (Description.hasAnyChanged.getValue() == true) {
			String message = mainApp.getIoOperations().updateDescriptionsInDB(
					descriptionTable.getItems());
			if (message.equals(IOOperations.successfullyUpdatedMessage)) {
				Dialogs.create()
						.title("Action successfully completed!")
						.masthead("Your changes saved.")
						.message(
								"All changes saved in DataBase "
										+ mainApp.getIoOperations().getDbName())
						.showInformation();
				Description.hasAnyChanged.setValue(false);
			} else {
				Dialogs.create().title("Action was not completed!")
						.masthead("Your changes didn't saved.")
						.message(message).showError();
			}

			message = mainApp.getIoOperations().deleteDescriptionsInDB(
					Description.getDeletedDescriptions());
			if (message.equals(IOOperations.successfullyUpdatedMessage)) {
				// Dialogs.create()
				// .title("Action successfully completed!")
				// .masthead("Your changes saved.")
				// .message(
				// "All deleted descriptions deleted from DataBase "
				// + mainApp.getIoOperations().getDbName())
				// .showInformation();
				Description.hasAnyChanged.setValue(Description.hasAnyChanged
						.getValue() | false);
				Description.getDeletedDescriptions().clear();
			} else {
				Dialogs.create()
						.title("Action was not completed!")
						.masthead(
								"Deleted descriptions didn't delete from DataBase.")
						.message(message).showError();
			}
		}
	}

	@FXML
	public void handleDiscardChanges() {
		Description.hasAnyChanged.setValue(false);
		mainApp.initialDescriptions();
		mainApp.setDescriptionOVerviewTableItems();
	}

	@FXML
	public void handleDeleteAllDescriptions() {
		if (mainApp.getIoOperations().getDbName() == null) {
			Dialogs.create().title("No DataBase added!")
					.masthead("Please Configure a new DataBase")
					.message("By clicking on 'Add a DataBase' button.")
					.showWarning();
			return;
		}

		Action dialogAction = Dialogs
				.create()
				.title("Your descriptions are going to be deleted.")
				.masthead("Are you sure about this action?")
				.message(
						"Your changes will not affect dataBase content right now.")
				.showConfirm();

		if (dialogAction == Actions.OK || dialogAction == Actions.YES) {
			while (descriptionTable.getItems().size() > 0) {
				descriptionTable.getItems().get(0).addToDeletedDescriptions();
				descriptionTable.getItems().remove(0);
			}
			Description.hasAnyChanged.setValue(true);
		}
	}


	public TableView<Description> getDescriptionTable() {
		return descriptionTable;
	}
}
