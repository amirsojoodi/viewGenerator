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

	public SimpleStringProperty getTableName() {
		return tableName;
	}

	public void setTableName(SimpleStringProperty tableName) {
		this.tableName = tableName;
	}

	public SimpleStringProperty getColumnName() {
		return columnName;
	}

	public void setColumnName(SimpleStringProperty columnName) {
		this.columnName = columnName;
	}

	public SimpleStringProperty getDescription() {
		return description;
	}

	public void setDescription(SimpleStringProperty description) {
		this.description = description;
	}

}
