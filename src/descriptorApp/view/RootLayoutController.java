package descriptorApp.view;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog.Actions;
import org.controlsfx.dialog.Dialogs;

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
		Dialogs.create().title("DescriptionApp").masthead("v1.0")
				.message("by Amir Hossein Sojoodi. ShirazU, ICTC - 2016")
				.showInformation();
	}

	@FXML
	private void handleExit() {
		if (Description.hasAnyChanged.getValue() == true) {
			Action dialogAction = Dialogs.create().title("Unsaved Data")
					.masthead("You have unsaved changes.")
					.message("Do you want to apply changes?").showConfirm();
			if (dialogAction == Actions.YES || dialogAction == Actions.OK) {
				handleSave();
			} else if (dialogAction == Actions.CLOSE
					|| dialogAction == Actions.CANCEL) {
				return;
			}
		}
		
		System.exit(0);
	}


}
