package descriptorApp.model;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;

public class DBView {

	private SimpleStringProperty dbConnectionName;
	private SimpleStringProperty viewName;
	private SimpleStringProperty primaryTable;
	private SimpleStringProperty whereClause;
	private SimpleStringProperty query;
	
	private ArrayList<DBTable> tables;

	// private ArrayList<DBJoin> joins;

	public DBView() {
		this(null, null, null, null, null);
	}

	public DBView(String dbConnectionName, String vName, String pTable,
			String whereClause, String query) {
		this.dbConnectionName = new SimpleStringProperty(dbConnectionName);
		this.viewName = new SimpleStringProperty(vName);
		this.primaryTable = new SimpleStringProperty(pTable);
		this.whereClause = new SimpleStringProperty(whereClause);
		this.query = new SimpleStringProperty(query);

		tables = new ArrayList<DBTable>();
		// joins = new ArrayList<DBJoin>();
	}

	public String getDbConnectionName() {
		return dbConnectionName.get();
	}

	public void setDbConnectionName(String dbConnectionName) {
		this.dbConnectionName.set(dbConnectionName);
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

	public String getQuery() {
		String q = "CREATE VIEW " + getViewName();

		if (tables.size() > 0) {
			q += " AS SELECT ";
		}

		boolean commaAdded = false;
		for (DBTable dbTable : tables) {
			if (dbTable.getTableName().equals("*")) {
				q += "* ";
				break;
			}

			commaAdded = false;
			for (DBColumn dbColumn : dbTable.getColumns()) {
				if (dbColumn.getColumnName().equals("*")) {
					q += dbColumn.getTableName() + ".* ";
					break;
				}
				q += dbColumn.getTableName() + "." + dbColumn.getColumnName();
				if (dbColumn.getDescription() != null && dbColumn.getDescription().length() != 0) {
					q += " AS '" + dbColumn.getDescription() + "'";
				} else {
					q += " AS '" + dbColumn.getColumnName() + "'";
				}
				q += ", ";
				commaAdded = true;
			}
		}
		if (commaAdded) {
			q = q.substring(0, q.length() - 2);
		}
		
		if (primaryTable.get() != null) {
			q += " FROM " + primaryTable.get() + " ";
		}

		// for (DBJoin dbJoin : joins) {
		// q += "JOIN " + dbJoin.getTableName();
		// q += " ON " + dbJoin.getLeftTableName() + "."
		// + dbJoin.getLeftColumnName() + " = "
		// + dbJoin.getRightTableName() + "."
		// + dbJoin.getRightColumnName() + " ";
		// }

		if (whereClause.get() != null) {
			q += "WHERE ";
			q += whereClause.get();
		}
		q += ";";

		query.set(q);
		return q;
	}

	public void setQuery(String query) {
		this.query.set(query);
	}

	public ArrayList<DBTable> getTables() {
		return tables;
	}
	
	public void setTables(ArrayList<DBTable> tables) {
		this.tables = tables;
	}

	// public ArrayList<DBJoin> getJoins() {
	// return joins;
	// }
	//
	// public void setJoins(ArrayList<DBJoin> joins) {
	// this.joins = joins;
	// }

	public ArrayList<DBColumn> getColumnsOf(String dbTable) {
		for (DBTable table : getTables()) {
			if (table.getTableName().equals(dbTable)) {
				return table.getColumns();
			}
		}
		return null;
	}
}
