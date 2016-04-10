package descriptorApp.model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;

public class DBView {

	private SimpleStringProperty viewName;
	private SimpleStringProperty primaryTable;
	private SimpleStringProperty whereClause;

	private ArrayList<DBColumn> columns;

	private ArrayList<DBJoin> joins;

	public DBView() {
		this(null, null, null);
	}

	public DBView(String vName, String pTable, String whereClause) {
		this.viewName = new SimpleStringProperty(vName);
		this.primaryTable = new SimpleStringProperty(pTable);
		this.whereClause = new SimpleStringProperty(whereClause);

		columns = new ArrayList<DBColumn>();
		joins = new ArrayList<DBJoin>();
	}

	public String getViewName() {
		return viewName.get();
	}

	public void setViewName(String vName) {
		this.viewName.set(vName);
	}

	public String getPrimaryTable() {
		return primaryTable.get();
	}

	public void setPrimaryTable(String pTable) {
		this.primaryTable.set(pTable);
	}

	public String getWhereClause() {
		return whereClause.get();
	}

	public void setWhereClause(String whereClause) {
		this.whereClause.set(whereClause);
	}

	public ArrayList<DBColumn> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<DBColumn> columns) {
		this.columns = columns;
	}

	public ArrayList<DBJoin> getJoins() {
		return joins;
	}

	public void setJoins(ArrayList<DBJoin> joins) {
		this.joins = joins;
	}

}
