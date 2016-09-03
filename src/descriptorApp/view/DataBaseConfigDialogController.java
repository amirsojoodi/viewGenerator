package descriptorApp.view;

import java.util.StringTokenizer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import descriptorApp.MainApp;
import descriptorApp.model.DBConnection;
import descriptorApp.model.IOOperations;

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

	TreeView<String> treeView;

	@FXML
	VBox vBox;

	TreeItem<String> rootNode;

	private Stage dialogStage;
	MainApp mainApp;
	private boolean okClicked = false;

	public final static String connectionsTreeRootName = "Connections";

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		messages.setEditable(false);
		rootNode = new TreeItem<>(connectionsTreeRootName);

		rootNode.setExpanded(true);

		treeView = new TreeView<String>(rootNode);
		treeView.setEditable(true);
		treeView.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());
		/*
		 * vBox.setAlignment(Pos.TOP_LEFT);
		 */
		vBox.getChildren().add(treeView);

		treeView.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showConnectionDetails(
								newValue, oldValue));

	}

	private void showConnectionDetails(TreeItem<String> newValue,
			TreeItem<String> oldValue) {

		if (oldValue != null
				&& !oldValue.getValue().equals(connectionsTreeRootName)) {
			String connectionName = oldValue.getValue();
			for (DBConnection dbConnection : mainApp.getConnectionData()) {
				if (dbConnection.getConnectionName().equals(connectionName)) {
					dbConnection.setServerIP(serverIP.getText());
					dbConnection.setServerPort(serverPort.getText());
					dbConnection.setDbName(dataBaseName.getText());
					dbConnection.setDbUsername(userName.getText());
					dbConnection.setDbPassword(hash(password.getText()));
					break;
				}
			}
		} else {
			// dbConnection.setServerIP("");
			// dbConnection.setServerPort("");
			// dbConnection.setDbName("");
			// dbConnection.setDbUsername("");
			// dbConnection.setDbPassword("");
		}

		if (newValue != null
				&& !newValue.getValue().equals(connectionsTreeRootName)) {
			String connectionName = newValue.getValue();
			for (DBConnection dbConnection : mainApp.getConnectionData()) {
				if (dbConnection.getConnectionName().equals(connectionName)) {
					mainApp.setActiveConnection(dbConnection);
					serverIP.setText(dbConnection.getServerIP());
					serverPort.setText(dbConnection.getServerPort());
					userName.setText(dbConnection.getDbUsername());
					password.setText(dehash(dbConnection.getDbPassword()));
					dataBaseName.setText(dbConnection.getDbName());
					break;
				}
			}
		} else {
			mainApp.setActiveConnection(null);
			serverIP.setText("");
			serverPort.setText("");
			userName.setText("");
			password.setText("");
			dataBaseName.setText("");
		}
	}

	private String hash(String text) {
		String st = "";
		byte b = 'a';
		for (int i = 0; i < text.length(); i++) {
			st += String.valueOf((int) text.charAt(i) ^ (int) b) + " ";
			// bytes[i] = (byte)(0xff & (text.getBytes()[i] ^ b));
		}
		return st;
	}
	
	private String dehash(String text){
		char[] chars = new char[100];
		
		StringTokenizer st = new StringTokenizer(text);
		
		byte b = 'a';
		int i = 0;
		while (st.hasMoreTokens()) {
			chars[i++] = (char)((char)Integer.parseInt(st.nextToken()) ^ (int) b); 
		}
		
		return new String(chars);
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		// this.dialogStage.setResizable(false);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		for (DBConnection dbConnection : mainApp.getConnectionData()) {
			TreeItem<String> connectionLeaf = new TreeItem<>(
					dbConnection.getConnectionName());
			rootNode.getChildren().add(connectionLeaf);
		}
		// if (mainApp.getConnectionData().size() == 0) {
		// DBConnection newConnection = new DBConnection();
		// newConnection.setConnectionName("Connection#1");
		// mainApp.getConnectionData().add(newConnection);
		// }

		treeView.getSelectionModel().selectFirst();
	}

	@FXML
	private void handleSave() {
		String connectionName = treeView.getSelectionModel().getSelectedItem()
				.getValue();
		if (connectionName != null) {
			for (DBConnection dbConnection : mainApp.getConnectionData()) {
				if (dbConnection.getConnectionName().equals(connectionName)) {
					dbConnection.setServerIP(serverIP.getText());
					dbConnection.setServerPort(serverPort.getText());
					dbConnection.setDbUsername(userName.getText());
					dbConnection.setDbPassword(hash(password.getText()));
					dbConnection.setDbName(dataBaseName.getText());
					break;
				}
			}
		}
		mainApp.getIoOperations().saveConnectionDataToFile();
	}

	@FXML
	private void handleNew() {
		DBConnection newConnection = new DBConnection();
		mainApp.getConnectionData().add(newConnection);
		String connectionName = "Connection#"
				+ mainApp.getConnectionData().size();
		newConnection.setConnectionName(connectionName);
		rootNode.getChildren().add(new TreeItem<>(connectionName));
		treeView.getSelectionModel().selectLast();
	}

	@FXML
	private void handleDelete() {
		String connectionName = treeView.getSelectionModel().getSelectedItem()
				.getValue();
		if (connectionName != null) {
			for (DBConnection dbConnection : mainApp.getConnectionData()) {
				if (dbConnection.getConnectionName().equals(connectionName)) {
					mainApp.getConnectionData().remove(dbConnection);
					break;
				}
			}

			for (TreeItem<String> leaf : rootNode.getChildren()) {
				if (leaf.getValue().equals(connectionName)) {
					rootNode.getChildren().remove(leaf);
					break;
				}
			}
		}
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

			handleCheckConnection();

			if (messages.getText().equals(
					IOOperations.connectionTestSuccessfullMessage)) {
				okClicked = true;
				dialogStage.close();
			}
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
		mainApp.setActiveConnection(null);
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

		if (serverIP.getText() == null || serverIP.getText().length() == 0) {
			errorMessage += "No valid server address!\n";
		}

		if (serverPort.getText() == null || serverPort.getText().length() == 0) {
			errorMessage += "No valid server port!\n";
		}

		if (dataBaseName.getText() == null
				|| dataBaseName.getText().length() == 0) {
			errorMessage += "No valid data base name!\n";
		}

		if (userName.getText() == null || userName.getText().length() == 0) {
			errorMessage += "No valid username!\n";
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

	private final class TextFieldTreeCellImpl extends TreeCell<String> {

		private TextField textField;
		String oldValue;

		public TextFieldTreeCellImpl() {
		}

		@Override
		public void startEdit() {
			super.startEdit();
			if (textField == null) {
				createTextField();
			}
			setText(null);
			setGraphic(textField);
			textField.selectAll();
		}

		@Override
		public void cancelEdit() {
			super.cancelEdit();
			setText((String) getItem());
			setGraphic(getTreeItem().getGraphic());
		}

		@Override
		public void updateItem(String item, boolean empty) {
			oldValue = getString();
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				if (isEditing()) {
					if (textField != null) {
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				} else {
					setText(getString());
					setGraphic(getTreeItem().getGraphic());

					if (!oldValue.equals(getString())) {
						for (DBConnection dbConnection : mainApp
								.getConnectionData()) {
							if (dbConnection.getConnectionName().equals(
									oldValue)) {
								dbConnection.setConnectionName(getString());
							}
						}
					}
				}
			}
		}

		private void createTextField() {

			textField = new TextField(getString());
			textField.setOnKeyReleased((KeyEvent t) -> {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			});
		}

		private String getString() {
			return getItem() == null ? "" : getItem().toString();
		}

	}

}