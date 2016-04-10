package descriptorApp.model;

import javafx.beans.property.SimpleStringProperty;

public class DBJoin {

	private SimpleStringProperty tableName;
	private SimpleStringProperty rightTableName;
	private SimpleStringProperty rightColumnName;
	private SimpleStringProperty leftTableName;
	private SimpleStringProperty leftColumnName;

	public DBJoin() {
		this(null, null, null, null, null);
	}

	public DBJoin(String tName, String rtName, String rcName, String ltName,
			String lcName) {
		tableName = new SimpleStringProperty(tName);
		rightTableName = new SimpleStringProperty(rtName);
		rightColumnName = new SimpleStringProperty(rcName);
		leftTableName = new SimpleStringProperty(ltName);
		leftColumnName = new SimpleStringProperty(lcName);
	}
	
	public String getTableName() {
		return tableName.get();
	}

	public void setTableName(String vName) {
		this.tableName.set(vName);
	}

	public String getRightTableName() {
		return rightTableName.get();
	}

	public void setRightTableName(String pTable) {
		this.rightTableName.set(pTable);
	}

	public String getRightColumnName() {
		return rightColumnName.get();
	}

	public void setRightColumnName(String rightColumnName) {
		this.rightColumnName.set(rightColumnName);
	}
	
	public String getLeftTableName() {
		return leftTableName.get();
	}

	public void setLeftTableName(String leftTableName) {
		this.leftTableName.set(leftTableName);
	}

	public String getLeftColumnName() {
		return leftColumnName.get();
	}

	public void setLeftColumnName(String leftColumnName) {
		this.leftColumnName.set(leftColumnName);
	}
}
