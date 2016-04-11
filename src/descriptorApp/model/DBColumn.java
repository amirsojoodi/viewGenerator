package descriptorApp.model;

import javafx.beans.property.SimpleStringProperty;

public class DBColumn {

	private SimpleStringProperty tableName;
	private SimpleStringProperty columnName;
	private SimpleStringProperty description;
	
	public DBColumn(){
		this(null, null, null);
	}
	
	public DBColumn(String tName, String cName, String desc){
		tableName = new SimpleStringProperty(tName);
		columnName = new SimpleStringProperty(cName);
		description = new SimpleStringProperty(desc);
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
}
