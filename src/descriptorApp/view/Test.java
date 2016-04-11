package descriptorApp.view;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Test extends Application {

	List<Employee> employees = Arrays.<Employee> asList(new Employee(
			"Ethan Williams", "ethan.williams@example.com"), new Employee(
			"Emma Jones", "emma.jones@example.com"), new Employee(
			"Michael Brown", "michael.brown@example.com"), new Employee(
			"Anna Black", "anna.black@example.com"), new Employee(
			"Rodger York", "roger.york@example.com"), new Employee(
			"Susan Collins", "susan.collins@example.com"));

	final TreeItem<Employee> root = new TreeItem<>(new Employee(
			"Sales Department", ""));

	public static void main(String[] args) {
		Application.launch(Test.class, args);
	}

	@Override
	public void start(Stage stage) {
		root.setExpanded(true);
		employees.stream().forEach((employee) -> {
			root.getChildren().add(new TreeItem<>(employee));
		});
		Scene scene = new Scene(new Group(), 400, 400);
		Group sceneRoot = (Group) scene.getRoot();

		TreeTableColumn<Employee, String> empColumn = new TreeTableColumn<>(
				"Employee");
		empColumn.setPrefWidth(150);
		empColumn
				.setCellValueFactory((
						TreeTableColumn.CellDataFeatures<Employee, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getName()));

		TreeTableColumn<Employee, String> emailColumn = new TreeTableColumn<>(
				"Email");
		emailColumn.setPrefWidth(190);
		emailColumn
				.setCellValueFactory((
						TreeTableColumn.CellDataFeatures<Employee, String> param) -> new ReadOnlyStringWrapper(
						param.getValue().getValue().getEmail()));

		TreeTableColumn<Employee, Boolean> column = new TreeTableColumn<>("");
		column.setEditable(true);

		column.setCellValueFactory((
				TreeTableColumn.CellDataFeatures<Employee, Boolean> param) -> new SimpleBooleanProperty(
				param.getValue().getValue().getChosen()));
		
		column.setCellFactory(new Callback<TreeTableColumn<Employee, Boolean>, TreeTableCell<Employee, Boolean>>() {
			@Override
			public TreeTableCell<Employee, Boolean> call(
					TreeTableColumn<Employee, Boolean> p) {
				CheckBoxTreeTableCell<Employee, Boolean> cell = new CheckBoxTreeTableCell<Employee, Boolean>();
				cell.setAlignment(Pos.CENTER);
				cell.setEditable(true);
				return cell;
			}
		});

		TreeTableView<Employee> treeTableView = new TreeTableView<>(root);
		treeTableView.getColumns().setAll(empColumn, emailColumn, column);
		treeTableView.setEditable(true);
		sceneRoot.getChildren().add(treeTableView);
		stage.setScene(scene);
		stage.show();
	}

	public class Employee {

		private SimpleStringProperty name;
		private SimpleStringProperty email;
		private SimpleBooleanProperty chosen;

		public SimpleStringProperty nameProperty() {
			if (name == null) {
				name = new SimpleStringProperty(this, "name");
			}
			return name;
		}

		public SimpleStringProperty emailProperty() {
			if (email == null) {
				email = new SimpleStringProperty(this, "email");
			}
			return email;
		}

		private Employee(String name, String email) {
			this.name = new SimpleStringProperty(name);
			this.email = new SimpleStringProperty(email);
			this.chosen = new SimpleBooleanProperty(false);
		}

		public String getName() {
			return name.get();
		}

		public void setName(String fName) {
			name.set(fName);
		}

		public String getEmail() {
			return email.get();
		}

		public void setEmail(String fName) {
			email.set(fName);
		}

		public boolean getChosen() {
			return chosen.get();
		}

		public void setChosen(boolean chosen) {
			this.chosen.set(chosen);
		}
	}
}