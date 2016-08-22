package descriptorApp.view;

import java.util.ArrayList;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import descriptorApp.MainApp;
import descriptorApp.model.DBColumn;
import descriptorApp.model.DBTable;
import descriptorApp.model.DBView;
import descriptorApp.model.Description;

public class ViewCreatorDialogController {

	@FXML
	private TextArea queryTextArea;

	@FXML
	private TextArea conditionTextArea;

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

	TreeItem<String> rootTableNode;

	private TreeView<String> tablesTreeView;

	private DBView selectedView;
	private Stage dialogStage;
	MainApp mainApp;
	private boolean okClicked = false;

	private SimpleStringProperty queryText;

	public final static String tableTreeRootName = "Tables";
	public final static String preMessageTextArea = "Generated Query";

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@SuppressWarnings("unchecked")
	@FXML
	private void initialize() {
		queryText = new SimpleStringProperty("");
		queryTextArea.setEditable(false);
		queryTextArea.setWrapText(true);
		queryTextArea.textProperty().bind(queryText);

		rootTableNode = new TreeItem<String>(tableTreeRootName);
		rootTableNode.setExpanded(true);

		rootDescriptionNode = new TreeItem<>();
		rootDescriptionNode.setExpanded(true);

		// tablesTreeView = new TreeView<String>(rootTableNode); //TODO set &
		// unset
		tablesTreeView = new TreeView<String>();
		tablesTreeView.setEditable(false);
		tablesTreeView
				.setCellFactory((TreeView<String> p) -> new TextFieldTreeCellImpl());

		descriptionTreeTable = new TreeTableView<>();
		// descriptionTreeTable = new TreeTableView<>(rootDescriptionNode);
		// //TODO set & unset
		descriptionTreeTable.setPrefHeight(605);

		vBoxTable.getChildren().add(tablesTreeView);
		vBoxTable.setAlignment(Pos.TOP_LEFT);
		vBoxTreeTable.getChildren().add(descriptionTreeTable);

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
		understandableNameColumn.setPrefWidth(218);
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
		selectColumn.setPrefWidth(40);

		selectColumn
				.setCellValueFactory((
						TreeTableColumn.CellDataFeatures<DBColumn, Boolean> param) -> {
					if (param.getValue() != null
							&& param.getValue().getValue() != null) {
						return new SimpleBooleanProperty(param.getValue()
								.getValue().getChosen());
					} else {
						return null;
					}
				});

		selectColumn
				.setCellFactory(new Callback<TreeTableColumn<DBColumn, Boolean>, TreeTableCell<DBColumn, Boolean>>() {
					@Override
					public TreeTableCell<DBColumn, Boolean> call(
							TreeTableColumn<DBColumn, Boolean> p) {

						CheckBoxTreeTableCell<DBColumn, Boolean> cell = new CheckBoxTreeTableCell<DBColumn, Boolean>();

						cell.setAlignment(Pos.CENTER);
						cell.setEditable(false);
						return cell;
					}

				});
		/*
		 * selectColumn.setOnMouseReleased(new EventHandler<MouseEvent>() {
		 * 
		 * @Override public void handle(MouseEvent event) {
		 * System.out.println("What the hell"); } });
		 */
		descriptionTreeTable.getColumns().setAll(columnNameColumn,
				understandableNameColumn, selectColumn);

		descriptionTreeTable.setEditable(true);

		descriptionTreeTable
				.getSelectionModel()
				.selectedItemProperty()
				.addListener(
						(observable, oldValue, newValue) -> changeViewDetails(
								newValue, oldValue));

		descriptionTreeTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (descriptionTreeTable.getSelectionModel()
						.selectedItemProperty().getValue() == null) {
					return;
				}
				boolean chosen = descriptionTreeTable.getSelectionModel()
						.selectedItemProperty().getValue().getValue()
						.getChosen();

				int index = descriptionTreeTable.getSelectionModel()
						.selectedIndexProperty().get() - 1;

				ArrayList<DBColumn> columns = new ArrayList<>();
				for (TreeItem<DBColumn> dbColumn : rootDescriptionNode
						.getChildren()) {
					DBColumn tmpDBColumn = new DBColumn(dbColumn.getValue());
					columns.add(tmpDBColumn);
					if (index == -1) {
						tmpDBColumn.setChosen(!chosen);
					}
				}

				if (index > -1) {
					columns.get(index).setChosen(!chosen);
				} else {
					rootDescriptionNode.setValue(new DBColumn(
							rootDescriptionNode.getValue().getTableName(),
							rootDescriptionNode.getValue().getTableName(),
							null, !chosen));
				}

				rootDescriptionNode.getChildren().clear();
				descriptionTreeTable.setRoot(null);

				for (DBColumn dbColumn : columns) {
					TreeItem<DBColumn> leaf = new TreeItem<DBColumn>(dbColumn);
					rootDescriptionNode.getChildren().add(leaf);
				}
				descriptionTreeTable.setRoot(rootDescriptionNode);

				saveViewDetails();
			}
		});

	}

	protected void saveViewDetails() {

		String dbTableName = tablesTreeView.getSelectionModel()
				.selectedItemProperty().getValue().getValue();

		if (selectedView.getPrimaryTable() == null) {
			selectedView.setPrimaryTable(dbTableName);
		}
		boolean found = false;
		for (DBTable dbTable : selectedView.getTables()) {
			if (dbTable.getTableName().equals(dbTableName)) {

				dbTable.getColumns().clear();

				for (TreeItem<DBColumn> dbColumnTreeItem : rootDescriptionNode
						.getChildren()) {

					if (dbColumnTreeItem.getValue().getChosen()) {
						dbTable.getColumns().add(dbColumnTreeItem.getValue());
					}
				}

				found = true;
				break;
			}
		}

		if (!found) {
			DBTable newDBTable = new DBTable(dbTableName);
			selectedView.getTables().add(newDBTable);
			for (TreeItem<DBColumn> dbColumnTreeItem : rootDescriptionNode
					.getChildren()) {

				if (dbColumnTreeItem.getValue().getChosen()) {
					newDBTable.getColumns().add(dbColumnTreeItem.getValue());
				}
			}
		}

		setQueryText(selectedView.getQuery());
	}

	private void changeViewDetails(TreeItem<DBColumn> newValue,
			TreeItem<DBColumn> oldValue) {

		if (oldValue != null) {

		} else {
			//
		}

		if (newValue != null) {
			/*
			 * System.out.println(newValue);
			 * System.out.println(newValue.getValue());
			 * System.out.println(newValue.getValue().getChosen());
			 */
		}

	}

	/*
	 * private void showViewDetails(TreeItem<String> newValue, TreeItem<String>
	 * oldValue) {
	 * 
	 * if (oldValue != null && !oldValue.getValue().equals(viewTreeRootName)) {
	 * String viewName = oldValue.getValue(); for (DBView dbView :
	 * mainApp.getViewData()) { if (dbView.getViewName().equals(viewName)) { //
	 * TODO save view details to dbView String tableName =
	 * treeView.getSelectionModel() .getSelectedItem().getValue();
	 * 
	 * } } } else {
	 * 
	 * }
	 * 
	 * if (newValue != null && !newValue.getValue().equals(viewTreeRootName)) {
	 * tablesTreeView.setRoot(rootTableNode);
	 * rootTableNode.getChildren().clear(); String viewName =
	 * newValue.getValue(); for (DBView dbView : mainApp.getViewData()) { if
	 * (dbView.getViewName().equals(viewName)) { selectedView = dbView; // TODO
	 * get view details from dbView initialTablesTreeView(); for (DBTable
	 * dbTable : dbView.getTables()) { TreeItem<String> leaf = new
	 * TreeItem<String>( dbTable.getTableName()); // leaf.setSelected(true);
	 * rootTableNode.getChildren().add(leaf); } for (String tableName : mainApp
	 * .getAllTablesAndColumnsFromDB().keySet()) { boolean found = false; for
	 * (TreeItem<String> leaf : rootTableNode .getChildren()) { if
	 * (leaf.getValue().equals(tableName)) { found = true; break; } } if
	 * (!found) { TreeItem<String> leaf = new TreeItem<String>( tableName); //
	 * leaf.setSelected(false); rootTableNode.getChildren().add(leaf); } }
	 * break; } } } else { rootTableNode.getChildren().clear();
	 * tablesTreeView.setRoot(null); selectedView = null; }
	 * 
	 * // tablesTreeView.setEditable(false); // tablesTreeView.setDisable(true);
	 * }
	 */

	private void showTableDetails(TreeItem<String> newValue,
			TreeItem<String> oldValue) {
		if (oldValue != null && !oldValue.getValue().equals(tableTreeRootName)) {
			// TODO
		} else {

		}

		if (newValue != null && !newValue.getValue().equals(tableTreeRootName)) {
			String tableName = newValue.getValue();
			if (selectedView == null) {
				return;
			}
			rootDescriptionNode.setValue(new DBColumn(tableName, tableName,
					null, false));
			rootDescriptionNode.getChildren().clear();
			descriptionTreeTable.setRoot(rootDescriptionNode);
			for (Description description : mainApp.getDescriptionData()) {
				if (!description.getTableName().equals(tableName)) {
					continue;
				}
				TreeItem<DBColumn> leaf = new TreeItem<DBColumn>(new DBColumn(
						description.getTableName(),
						description.getColumnName(),
						description.getUnderstandableName(), false));
				rootDescriptionNode.getChildren().add(leaf);
			}

			if (selectedView.getColumnsOf(tableName) != null) {
				for (DBColumn dbColumn : selectedView.getColumnsOf(tableName)) {
					for (TreeItem<DBColumn> treeItemDBColumn : rootDescriptionNode
							.getChildren()) {
						if (treeItemDBColumn.getValue().getColumnName()
								.equals(dbColumn.getColumnName())) {
							treeItemDBColumn.getValue().setChosen(true);
							break;
						}
					}
				}
			}
		} else {
			rootDescriptionNode.getChildren().clear();
			descriptionTreeTable.setRoot(null);
		}
	}

	public String getQueryText() {
		return queryText.get();
	}

	public void setQueryText(String queryText) {
		if (queryText == null) {
			this.queryText.set(preMessageTextArea + queryText);
		} else {
			this.queryText.set(queryText);
		}
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setResizable(false);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		
		// if (mainApp.getViewData().size() == 0) {
		// DBView newView = new DBView();
		// newView.setViewName("View#1");
		// mainApp.getViewData().add(newView);
		// }

		// treeView.getSelectionModel().selectFirst();
	}

	public void initialTablesTreeView() {
		rootTableNode.getChildren().clear();
		for (String tableName : mainApp.getTablesAndColumns().keySet()) {
			TreeItem<String> leaf = new TreeItem<String>(tableName);
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
				DBColumn tmpDBColumn = new DBColumn(tableName, columnName,
						null, false);

			}
		}

		// When selecting table names:
		// for (Description description : mainApp.getDescriptionData()) {
		//
		// }

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
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Fields!");
			alert.setHeaderText("Please correct invalid fields!");
			alert.setContentText("Action was not completed!");
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
//						for (DBView dbView : mainApp.getViewData()) {
//							if (dbView.getViewName().equals(oldValue)) {
//								dbView.setViewName(getString());
//							}
//						}
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