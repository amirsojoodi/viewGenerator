package descriptorApp.view;

import java.awt.Desktop;
import java.net.URL;
import java.util.ArrayList;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private TextArea viewNameTextArea;

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
	private SimpleStringProperty conditionText;
	private SimpleStringProperty viewNameText;

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
		queryTextArea.setEditable(true);
		queryTextArea.setWrapText(true);
		queryTextArea
				.setPromptText("Generated Query (you can edit this query)");

		queryTextArea.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					final ObservableValue<? extends String> observable,
					final String oldValue, final String newValue) {
				queryText.set(newValue);
			}
		});

		conditionText = new SimpleStringProperty("");
		conditionTextArea.setEditable(true);
		conditionTextArea.setWrapText(true);
		conditionTextArea.setPromptText("Everything you put here, comes after "
				+ "WHERE clause, For example : age > 20");

		conditionTextArea.textProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(
							final ObservableValue<? extends String> observable,
							final String oldValue, final String newValue) {
						conditionText.set(newValue);
						selectedView.setWhereClause(conditionText.get());
						setQueryText(selectedView.getQuery());
					}
				});

		viewNameText = new SimpleStringProperty("");
		viewNameTextArea.setEditable(true);
		viewNameTextArea.setWrapText(true);
		viewNameTextArea.setPromptText("View Name");

		viewNameTextArea.textProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(
							final ObservableValue<? extends String> observable,
							final String oldValue, final String newValue) {
						viewNameText.set(newValue);
						selectedView.setViewName(viewNameText.get());
						setQueryText(selectedView.getQuery());
					}
				});

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

				boolean ifAny = false;
				for (TreeItem<DBColumn> dbColumnTreeItem : rootDescriptionNode
						.getChildren()) {

					if (dbColumnTreeItem.getValue().getChosen()) {
						dbTable.getColumns().add(dbColumnTreeItem.getValue());
						ifAny = true;
					}
				}

				if (!ifAny) {
					selectedView.getTables().remove(dbTable);

					if (selectedView.getTables() != null
							&& selectedView.getTables().size() > 0
							&& selectedView.getTables().get(0) != null) {
						selectedView.setPrimaryTable(selectedView.getTables()
								.get(0).getTableName());
					} else {
						selectedView.setPrimaryTable(null);
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

	public void setQueryText(String queryTxt) {
		queryText.set(queryTxt);
		queryTextArea.setText(queryTxt);
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setResizable(false);
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		initialTablesTreeView();
		selectedView = new DBView();
		selectedView.setDbConnectionName(mainApp.getActiveConnection()
				.getConnectionName());
	}

	public void initialTablesTreeView() {
		tablesTreeView.setRoot(rootTableNode);
		rootTableNode.getChildren().clear();
		for (String tableName : mainApp.getTablesAndColumns().keySet()) {
			TreeItem<String> leaf = new TreeItem<String>(tableName);
			rootTableNode.getChildren().add(leaf);
		}
		for (String tableName : mainApp.getAllTablesAndColumnsFromDB().keySet()) {
			boolean found = false;
			for (TreeItem<String> leaf : rootTableNode.getChildren()) {
				if (leaf.getValue().equals(tableName)) {
					found = true;
					break;
				}
			}
			if (!found) {
				TreeItem<String> leaf = new TreeItem<String>(tableName); //
				// leaf.setSelected(false);
				rootTableNode.getChildren().add(leaf);
			}
		}
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleOk() {
		if (isInputValid()) {
			boolean errorOccured = mainApp.getIoOperations().createView(
					queryText.get());
			if (!errorOccured) {
				okClicked = true;
				dialogStage.close();
			}
		}
	}

	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	@FXML
	private void handlePreview() {
		if (isInputValid()) {
			int startIndex = queryText.get().indexOf("SELECT");
			String[][] results = mainApp.getIoOperations().previewView(
					(queryText.get().substring(startIndex)).replaceFirst(
							"SELECT", "SELECT TOP 100"), 100);
		}
	}

	@FXML
	private void handleHelpWithConditions() {
		try {
			Desktop.getDesktop().browse(
					new URL("http://www.w3schools.com/sql/sql_where.asp")
							.toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isInputValid() {

		String errorMessage = "";

		if (queryText == null || queryText.get() == null
				|| queryText.get().length() == 0) {
			errorMessage += "No valid Query!\n";
		}

		if (viewNameText == null || viewNameText.get() == null
				|| viewNameText.get().length() == 0) {
			errorMessage += "No valid view name!\n";
		}

		if (errorMessage.length() == 0) {
			return true;
		} else {
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Fields!");
			alert.setHeaderText("Please correct invalid fields!");
			alert.setContentText(errorMessage);

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
						// for (DBView dbView : mainApp.getViewData()) {
						// if (dbView.getViewName().equals(oldValue)) {
						// dbView.setViewName(getString());
						// }
						// }
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