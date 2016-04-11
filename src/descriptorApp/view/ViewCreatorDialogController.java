package descriptorApp.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import org.controlsfx.dialog.Dialogs;

import descriptorApp.MainApp;
import descriptorApp.model.DBColumn;
import descriptorApp.model.DBTable;
import descriptorApp.model.DBView;
import descriptorApp.model.Description;
import descriptorApp.view.Test.Employee;

public class ViewCreatorDialogController {

	@FXML
	private TextArea query;

	@FXML
	private VBox vBox;

	@FXML
	private VBox vBoxTable;

	@FXML
	private VBox vBoxTreeTable;

	@FXML
	private TreeTableView<DBColumn> descriptionTreeTable;

	@FXML
	private TreeTableColumn<DBColumn, String> columnNameColumn;

	@FXML
	private TreeTableColumn<DBColumn, String> understandableNameColumn;

	@FXML
	private TreeTableColumn<DBColumn, Boolean> selectColumn;

	TreeItem<DBColumn> rootDescriptionNode;

	TreeItem<String> rootViewNode;

	CheckBoxTreeItem<String> rootTableNode;

	// For selecting Views!!
	private TreeView<String> treeView;

	private TreeView<String> tablesTreeView;

	private Stage dialogStage;
	MainApp mainApp;
	private boolean okClicked = false;

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void initialize() {
		query.setEditable(false);

		rootViewNode = new TreeItem<>("Views");
		rootViewNode.setExpanded(true);

		rootTableNode = new CheckBoxTreeItem<String>("Tables");
		rootTableNode.setExpanded(true);

		rootDescriptionNode = new TreeItem<>();
		rootDescriptionNode.setExpanded(true);

		treeView = new TreeView<String>(rootViewNode);
		treeView.setEditable(true);
		treeView.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());

		tablesTreeView = new TreeView<String>(rootTableNode);
		tablesTreeView.setEditable(true);
		tablesTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());

		descriptionTreeTable = new TreeTableView<>(rootDescriptionNode);
		descriptionTreeTable.setPrefHeight(605);

		vBox.getChildren().add(treeView);
		vBoxTable.getChildren().add(tablesTreeView);
		vBoxTreeTable.getChildren().add(descriptionTreeTable);

		treeView.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showViewDetails(
								newValue, oldValue));
		tablesTreeView
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> showTableDetails(
								newValue, oldValue));
		
		columnNameColumn = new TreeTableColumn<>("Column Name");
		columnNameColumn.setPrefWidth(180);
		columnNameColumn.setCellValueFactory((
				TreeTableColumn.CellDataFeatures<DBColumn, String> param) -> {
			if (param != null && param.getValue() != null
					&& param.getValue().getValue() != null) {
				return new ReadOnlyStringWrapper(param.getValue().getValue()
						.getColumnName());
			} else {
				return null;
			}
		});

		understandableNameColumn = new TreeTableColumn<>("Description");
		understandableNameColumn.setPrefWidth(220);
		understandableNameColumn.setCellValueFactory((
				TreeTableColumn.CellDataFeatures<DBColumn, String> param) -> {
			if (param != null && param.getValue() != null
					&& param.getValue().getValue() != null) {
				return new ReadOnlyStringWrapper(param.getValue().getValue()
						.getDescription());
			} else {
				return null;
			}
		});

		selectColumn = new TreeTableColumn<>("");
		selectColumn.setEditable(true);

		selectColumn
				.setCellValueFactory((
						TreeTableColumn.CellDataFeatures<DBColumn, Boolean> param) -> new SimpleBooleanProperty());

		selectColumn
				.setCellFactory(new Callback<TreeTableColumn<DBColumn, Boolean>, TreeTableCell<DBColumn, Boolean>>() {
					@Override
					public TreeTableCell<DBColumn, Boolean> call(
							TreeTableColumn<DBColumn, Boolean> p) {
						CheckBoxTreeTableCell<DBColumn, Boolean> cell = new CheckBoxTreeTableCell<DBColumn, Boolean>();
						cell.setAlignment(Pos.CENTER);
						cell.setEditable(true);
						return cell;
					}
				});

		descriptionTreeTable.getColumns().setAll(columnNameColumn,
				understandableNameColumn, selectColumn);
		descriptionTreeTable.setEditable(true);
	}

	private void showViewDetails(TreeItem<String> newValue,
			TreeItem<String> oldValue) {

		if (oldValue != null) {
			String viewName = oldValue.getValue();
			for (DBView dbView : mainApp.getViewData()) {
				if (dbView.getViewName().equals(viewName)) {
					// TODO save view details to dbView
					String tableName = treeView.getSelectionModel()
							.getSelectedItem().getValue();
					
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
			rootTableNode.getChildren().clear();
			String viewName = newValue.getValue();
			for (DBView dbView : mainApp.getViewData()) {
				if (dbView.getViewName().equals(viewName)) {
					// TODO get view details from dbView
					initialTablesTreeView();
					for (DBTable dbTable : dbView.getTables()) {
						
						
						CheckBoxTreeItem<String> leaf = new CheckBoxTreeItem<String>(
								dbTable.getTableName());
						leaf.setSelected(true);
						rootTableNode.getChildren().add(leaf);
					}
					for (String tableName : mainApp
							.getAllTablesAndColumnsFromDB().keySet()) {
						boolean found = false;
						for (TreeItem<String> leaf : rootTableNode
								.getChildren()) {
							if (leaf.getValue().equals(tableName)) {
								found = true;
								break;
							}
						}
						if (!found) {
							CheckBoxTreeItem<String> leaf = new CheckBoxTreeItem<String>(
									tableName);
							leaf.setSelected(false);
							rootTableNode.getChildren().add(leaf);
						}
					}
					break;
				}
			}
		} else {
			rootTableNode.getChildren().clear();
		}
	}

	private void showTableDetails(TreeItem<String> newValue,
			TreeItem<String> oldValue) {
		if (oldValue != null) {

		} else {

		}

		if (newValue != null) {
			String tableName = newValue.getValue();

		} else {

		}

		// employees.stream().forEach((employee) -> {
		// root.getChildren().add(new TreeItem<>(employee));
		// });

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
			rootViewNode.getChildren().add(viewLeaf);
		}
		if (mainApp.getViewData().size() == 0) {
			DBView newView = new DBView();
			newView.setViewName("View #1");
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
		rootViewNode.getChildren().add(new TreeItem<>(viewName));
		treeView.getSelectionModel().selectLast();

		initialView(newView);
		initialTablesTreeView();

	}

	public void initialTablesTreeView() {
		rootTableNode.getChildren().clear();
		for (String tableName : mainApp.getTablesAndColumns().keySet()) {
			CheckBoxTreeItem<String> leaf = new CheckBoxTreeItem<String>(
					tableName);
			rootTableNode.getChildren().add(leaf);
		}

	}

	private void initialView(DBView newView) {

		// if (mainApp.getAllTablesAndColumnsFromDB().size() == 0) {
		// mainApp.getIoOperations().loadAllColumnsFromDB(mainApp.getAllTablesAndColumnsFromDB(),
		// false);
		// }

		for (String tableName : mainApp.getTablesAndColumns().keySet()) {

			DBTable tmpDBTable = new DBTable(tableName);

			for (String columnName : mainApp.getTablesAndColumns().get(
					tableName)) {
				DBColumn tmpDBColumn = new DBColumn(tableName, columnName, null);

			}
		}

		// When selecting table names:
		// for (Description description : mainApp.getDescriptionData()) {
		//
		// }

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

			for (TreeItem<String> leaf : rootViewNode.getChildren()) {
				if (leaf.getValue().equals(viewName)) {
					rootViewNode.getChildren().remove(leaf);
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