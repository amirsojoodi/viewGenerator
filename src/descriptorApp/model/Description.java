package descriptorApp.model;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Description {

	private long descriptionID;
	private StringProperty tableName;
	private StringProperty columnName;
	private StringProperty understandableName;
	private StringProperty description;

	private boolean changed;
	public static BooleanProperty hasAnyChanged;

	private static ArrayList<Description> allDescriptions;
	private static ArrayList<Description> deletedDescriptions;
	private static long lastDescriptionID;
	
	static {
		allDescriptions = new ArrayList<Description>();
		deletedDescriptions = new ArrayList<Description>();
		hasAnyChanged = new SimpleBooleanProperty();
		lastDescriptionID = 0;
	}

	public Description() {
		this(null, null, null, null);
		descriptionID = lastDescriptionID;
		lastDescriptionID++;
	}

	public Description(String tableName, String columnName,
			String understandableName, String description) {

		this.tableName = new SimpleStringProperty(tableName);
		this.columnName = new SimpleStringProperty(columnName);
		this.understandableName = new SimpleStringProperty(understandableName);
		this.description = new SimpleStringProperty(description);

		changed = false;

		addToAllDescriptions(this);
	}
	
	public Description(long descriptionID, String tableName, String columnName,
			String understandableName, String description) {
		this(tableName, columnName, understandableName, description);
		this.descriptionID = descriptionID;
		if(lastDescriptionID <= descriptionID){
			lastDescriptionID = descriptionID + 1;
		}
	}

	public Description(String tableName, String columnName) {
		this(tableName, columnName, "", "");
		descriptionID = lastDescriptionID;
		lastDescriptionID++;
	}

	public StringProperty tableNameProperty() {
		return tableName;
	}

	public String getTableName() {
		return tableNameProperty().get();
	}

	public void setTableName(final String tableNameString) {
		tableNameProperty().set(tableNameString);
	}

	public StringProperty columnNameProperty() {
		return columnName;
	}

	public String getColumnName() {
		return columnNameProperty().get();
	}

	public void setColumnName(final String columnNameString) {
		columnNameProperty().set(columnNameString);
	}

	public StringProperty understandableNameProperty() {
		return understandableName;
	}

	public String getUnderstandableName() {
		return understandableNameProperty().get();
	}

	public void setUnderstandableName(final String understandableName) {
		understandableNameProperty().set(understandableName);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	public String getDescription() {
		return descriptionProperty().get();
	}

	public void setDescription(final String description) {
		descriptionProperty().set(description);
	}

	public boolean isChanged() {
		return changed;
	}

	public void setHasChanged(boolean hasChanged) {
		this.changed = hasChanged;
	}

	public static ArrayList<Description> getAllDescriptions() {
		return allDescriptions;
	}

	private static void addToAllDescriptions(Description description) {
		allDescriptions.add(description);
	}

	public long getDescriptionID() {
		return descriptionID;
	}

	public void setDescriptionID(long descriptionID) {
		this.descriptionID = descriptionID;
		if(descriptionID >= lastDescriptionID){
			lastDescriptionID = descriptionID + 1;
		}
	}

	public static ArrayList<Description> getDeletedDescriptions() {
		return deletedDescriptions;
	}

	public static void setDeletedDescriptions(
			ArrayList<Description> deletedDescriptions) {
		Description.deletedDescriptions = deletedDescriptions;
	}
	
	public static void addToDeletedDescriptions(Description description){
		deletedDescriptions.add(description);
	}
	
	public void addToDeletedDescriptions(){
		deletedDescriptions.add(this);
	}
}
