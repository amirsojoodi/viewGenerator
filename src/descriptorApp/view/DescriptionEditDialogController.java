package descriptorApp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import descriptorApp.model.Description;

public class DescriptionEditDialogController {

	@FXML
	private TextField tableNameField;

	@FXML
	private TextField columnNameField;

	@FXML
	private TextField understandableNameField;
	
	@FXML
	private TextField descriptionField;

	private Stage dialogStage;
	private Description description;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		tableNameField.setEditable(false);
		columnNameField.setEditable(false);
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Sets the description to be edited in the dialog.
	 * 
	 * @param description
	 */
	public void setDescription(Description description) {
		this.description = description;

		tableNameField.setText(description.getTableName());
		columnNameField.setText(description.getColumnName());
		understandableNameField.setText(description.getUnderstandableName());
		descriptionField.setText(description.getDescription());
		
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks ok.
	 */
	@FXML
	private void handleOk() {
		if (isInputValid()) {
			description.setTableName(tableNameField.getText());
			description.setColumnName(columnNameField.getText());
			description.setUnderstandableName(understandableNameField.getText());
			description.setDescription(descriptionField.getText());

			okClicked = true;
			dialogStage.close();
			description.setHasChanged(true);
			Description.hasAnyChanged.setValue(true);
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (tableNameField.getText() == null
				|| tableNameField.getText().length() == 0) {
			errorMessage += "No valid tableName!\n";
		}

		if (columnNameField.getText() == null
				|| columnNameField.getText().length() == 0) {
			errorMessage += "No valid columnName!\n";
		}

		if (understandableNameField.getText() == null
				|| understandableNameField.getText().length() == 0) {
			errorMessage += "No valid understandableName!\n";
		}

		if (descriptionField.getText() == null
				|| descriptionField.getText().length() == 0) {
			errorMessage += "No valid description!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Fields");
			alert.setHeaderText("Please correct invalid fields");
			alert.setContentText("Invalide Fields.");
			alert.showAndWait();
			return false;
		}
	}
	
}