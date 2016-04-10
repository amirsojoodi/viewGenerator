package descriptorApp.view;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import descriptorApp.MainApp;
import descriptorApp.model.DBView;

public class ViewCreatorDialogController {

	@FXML
	private TextArea messages;

	// For selecting Views!!
	private TreeView<String> treeView;

	@FXML
	private VBox vBox;

	@FXML
	private VBox vBoxTable;

	@FXML
	private TableView<DBView> descriptionTable;

	@FXML
	private TableColumn<DBView, String> columnNameColumn;

	@FXML
	private TableColumn<DBView, String> understandableNameColumn;

	TreeItem<String> rootNode;

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
		rootNode = new TreeItem<>("Views");

		rootNode.setExpanded(true);

		treeView = new TreeView<String>(rootNode);
		treeView.setEditable(true);
		treeView.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());

		vBox.getChildren().add(treeView);

		treeView.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showViewDetails(
								newValue, oldValue));
	}

	private void showViewDetails(TreeItem<String> newValue,
			TreeItem<String> oldValue) {

		if (oldValue != null) {
			String viewName = oldValue.getValue();
			for (DBView dbView : mainApp.getViewData()) {
				if (dbView.getViewName().equals(viewName)) {
					// TODO save view details to dbView
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

		if (newValue != null) {
			String viewName = newValue.getValue();
			for (DBView dbView : mainApp.getViewData()) {
				if (dbView.getViewName().equals(viewName)) {
					// TODO get view details from dbView
					break;
				}
			}
		} else {
			// TODO empty areas
			// serverIP.setText("");
			// serverPort.setText("");
			// userName.setText("");
			// password.setText("");
			// dataBaseName.setText("");
		}
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setResizable(false);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		for (DBView dbView : mainApp.getViewData()) {
			TreeItem<String> viewLeaf = new TreeItem<String>(
					dbView.getViewName());
			rootNode.getChildren().add(viewLeaf);
		}
		if (mainApp.getViewData().size() == 0) {
			System.out.println("view Empty");
			DBView newView = new DBView();
			newView.setViewName("New View");
			mainApp.getViewData().add(newView);
		}
	}

	@FXML
	private void handleSave() {
		String viewName = treeView.getSelectionModel().getSelectedItem()
				.getValue();
		if (viewName != null) {
			for (DBView dbView : mainApp.getViewData()) {
				if (dbView.getViewName().equals(viewName)) {
					// TODO Save data to DBView
					break;
				}
			}
		}
		mainApp.getIoOperations().saveViewDataToFile();
	}

	@FXML
	private void handleNew() {
		DBView newView = new DBView();
		mainApp.getViewData().add(newView);
		String viewName = "View #" + mainApp.getViewData().size();
		newView.setViewName(viewName);
		rootNode.getChildren().add(new TreeItem<>(viewName));
		treeView.getSelectionModel().selectLast();
	}

	@FXML
	private void handleDelete() {
		String viewName = treeView.getSelectionModel().getSelectedItem()
				.getValue();
		if (viewName != null) {
			for (DBView dbView : mainApp.getViewData()) {
				if (dbView.getViewName().equals(viewName)) {
					mainApp.getViewData().remove(dbView);
					break;
				}
			}

			for (TreeItem<String> leaf : rootNode.getChildren()) {
				if (leaf.getValue().equals(viewName)) {
					rootNode.getChildren().remove(leaf);
					break;
				}
			}
		}
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleOk() {
		if (isInputValid()) {
			// TODO
			okClicked = true;
			dialogStage.close();
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	private boolean isInputValid() {

		// TODO

		String errorMessage = "";

		/*
		 * if (serverIP.getText() == null || serverIP.getText().length() == 0) {
		 * errorMessage += "No valid server address!\n"; }
		 * 
		 * if (serverPort.getText() == null || serverPort.getText().length() ==
		 * 0) { errorMessage += "No valid server port!\n"; }
		 * 
		 * if (dataBaseName.getText() == null || dataBaseName.getText().length()
		 * == 0) { errorMessage += "No valid data base name!\n"; }
		 * 
		 * if (userName.getText() == null || userName.getText().length() == 0) {
		 * errorMessage += "No valid username!\n"; }
		 */

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
						for (DBView dbView : mainApp.getViewData()) {
							if (dbView.getViewName().equals(oldValue)) {
								dbView.setViewName(getString());
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