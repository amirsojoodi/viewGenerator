package descriptorApp.view;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import descriptorApp.MainApp;

public class DataBaseConfigDialogController {

	@FXML
	private TextField serverIP;

	@FXML
	private TextField serverPort;

	@FXML
	private TextField dataBaseName;
	
	@FXML
	private TextField userName;

	@FXML
	private PasswordField password;
	
	@FXML
	private TextArea messages;

	
	private Stage dialogStage;
	MainApp mainApp;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		messages.setEditable(false);
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
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
			mainApp.getIoOperations().setServerIP(serverIP.getText());
			mainApp.getIoOperations().setServerPort(serverPort.getText());
			mainApp.getIoOperations().setDbName(dataBaseName.getText());
			mainApp.getIoOperations().setDbUsername(userName.getText());
			mainApp.getIoOperations().setDbPassword(password.getText());

			okClicked = true;
			dialogStage.close();
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
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCheckConnection() {
		if (isInputValid()) {
			mainApp.getIoOperations().setServerIP(serverIP.getText());
			mainApp.getIoOperations().setServerPort(serverPort.getText());
			mainApp.getIoOperations().setDbName(dataBaseName.getText());
			mainApp.getIoOperations().setDbUsername(userName.getText());
			mainApp.getIoOperations().setDbPassword(password.getText());

			messages.setText(mainApp.getIoOperations().checkConnection());
		}
	}

	/**
	 * Validates the user input in the text fields.
	 * 
	 * @return true if the input is valid
	 */
	private boolean isInputValid() {
		String errorMessage = "";

		if (serverIP.getText() == null
				|| serverIP.getText().length() == 0) {
			errorMessage += "No valid server address!\n";
		}

		if (serverPort.getText() == null
				|| serverPort.getText().length() == 0) {
			errorMessage += "No valid server port!\n";
		}

		if (dataBaseName.getText() == null
				|| dataBaseName.getText().length() == 0) {
			errorMessage += "No valid data base name!\n";
		}

		if (userName.getText() == null
				|| userName.getText().length() == 0) {
			errorMessage += "No valid username!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Dialogs.create().title("Invalid Fields")
					.masthead("Please correct invalid fields")
					.message(errorMessage).showError();
			return false;
		}
	}
	
}