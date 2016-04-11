package descriptorApp.model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;

public class DBTable {

	private SimpleStringProperty tableName;
	
	private ArrayList<DBColumn> columns;
	
	public DBTable(){
		this(null);
	}
	
	public DBTable(String tName){
		tableName = new SimpleStringProperty(tName);
		columns = new ArrayList<DBColumn>();
	}

	public String getTableName() {
		return tableName.get();
	}

	public void setTableName(String tableName) {
		this.tableName.set(tableName);
	}

	public ArrayList<DBColumn> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<DBColumn> columns) {
		this.columns = columns;
	}

}
