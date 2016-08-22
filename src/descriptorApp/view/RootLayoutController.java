package descriptorApp.view;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import descriptorApp.MainApp;
import descriptorApp.model.Description;

public class RootLayoutController {

	// Reference to the main application
	public MainApp mainApp;
	
	@FXML
	private MenuItem save;
	
	@FXML
	private MenuItem discardChanges;
	
	private DescriptionOverviewController descriptionOverviewController;

	@FXML
	private void initialize() {
		
		save.setDisable(true);
		discardChanges.setDisable(true);
		
		Description.hasAnyChanged
				.addListener((observable, oldValue, newValue) -> {
					if (newValue == true) {
						save.setDisable(false);
						discardChanges.setDisable(false);
					} else {
						save.setDisable(true);
						discardChanges.setDisable(true);
					}
				});
	}

	
	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void setDescriptionOverviewController(DescriptionOverviewController descriptionOverviewController){
		this.descriptionOverviewController = descriptionOverviewController;
	}

	@FXML
	private void handleNewDataBase() {
		descriptionOverviewController.handleAddDataBase();
	}
	
	@FXML
	private void handleNewDescription() {
		descriptionOverviewController.handleNewDescription();
	}

	@FXML
	private void handleDiscardChanges() {
		descriptionOverviewController.handleDiscardChanges();
	}
	
	@FXML
	private void handleSave() {
		descriptionOverviewController.handleApplyChanges();
	}

	@FXML
	private void handleDeleteDescription() {
		descriptionOverviewController.handleDeleteDescription();
	}
	
	@FXML
	private void handleDeleteAllDescriptions() {
		descriptionOverviewController.handleDeleteAllDescriptions();
	}
	
	@FXML
	private void handleEditDescription() {
		descriptionOverviewController.handleEditDescription();
	}

	@FXML
	private void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("DBDescriptionApp");
		alert.setHeaderText("v1.0");
		alert.setContentText("by Amir Hossein Sojoodi. ShirazU, ICTC - 2016");
		alert.showAndWait();
	}

	@FXML
	private void handleExit() {
		if (Description.hasAnyChanged.getValue() == true) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Unsaved Data");
			alert.setHeaderText("You have unsaved changes.");
			alert.setContentText("Do you want to apply changes?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK || result.get() == ButtonType.YES){
				handleSave();
			} else {
				return;
			}
		}
		
		System.exit(0);
	}


}
