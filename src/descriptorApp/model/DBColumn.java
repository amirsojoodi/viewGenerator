package descriptorApp.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class DBColumn {

	private SimpleStringProperty tableName;
	private SimpleStringProperty columnName;
	private SimpleStringProperty description;
	private SimpleBooleanProperty chosen;
	
	public DBColumn(){
		this(null, null, null, null);
	}
	
	public DBColumn(String tName, String cName, String desc, Boolean chsn){
		tableName = new SimpleStringProperty(tName);
		columnName = new SimpleStringProperty(cName);
		description = new SimpleStringProperty(desc);
		chosen = new SimpleBooleanProperty((chsn != null)? chsn : false);
	}
	
	public DBColumn(DBColumn dbColumn){
		tableName = new SimpleStringProperty(dbColumn.getTableName());
		columnName = new SimpleStringProperty(dbColumn.getColumnName());
		description = new SimpleStringProperty(dbColumn.getDescription());
		chosen = new SimpleBooleanProperty(dbColumn.getChosen());
	}

	public String getTableName() {
		return tableName.get();
	}

	public void setTableName(String tableName) {
		this.tableName.set(tableName);
	}

	public String getColumnName() {
		return columnName.get();
	}

	public void setColumnName(String columnName) {
		this.columnName.set(columnName);
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) {
		this.description.set(description);
	}
	
	public Boolean getChosen(){
		return chosen.get();
	}
	
	public void setChosen(Boolean chosen){
		this.chosen.set(chosen);
	}
}
