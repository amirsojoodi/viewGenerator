package descriptorApp.model;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;

public class DBConnection {

	private SimpleStringProperty connectionName;
	private SimpleStringProperty serverIP;
	private SimpleStringProperty serverPort;
	private SimpleStringProperty dbName;
	private SimpleStringProperty dbUsername;
	private SimpleStringProperty dbPassword;

	public DBConnection() {
		this(null, null, null, null, null, null);
	}

	public DBConnection(String connectionName, String serverIP,
			String serverPort, String dbName, String dbUsername,
			String dbPassword) {
		this.connectionName = new SimpleStringProperty(connectionName);
		this.serverIP = new SimpleStringProperty(serverIP);
		this.serverPort = new SimpleStringProperty(serverPort);
		this.dbName = new SimpleStringProperty(dbName);
		this.dbUsername = new SimpleStringProperty(dbUsername);
		this.dbPassword = new SimpleStringProperty(dbPassword);
	}

	public String getConnectionName() {
		return connectionName.get();
	}

	public void setConnectionName(String connectionName) {
		if(this.connectionName.get() != null && !this.connectionName.get().equals(connectionName)) {
			File tmpFile = new File(this.connectionName.get() + "-" + IOOperations.viewsFilePath);
			if(tmpFile.exists()){
				tmpFile.renameTo(new File(connectionName + "-" + IOOperations.viewsFilePath));
			}
		}
		this.connectionName.set(connectionName);
	}

	public String getServerIP() {
		return serverIP.get();
	}

	public void setServerIP(String serverIP) {
		this.serverIP.set(serverIP);
	}

	public String getServerPort() {
		return serverPort.get();
	}

	public void setServerPort(String serverPort) {
		this.serverPort.set(serverPort);
	}

	public String getDbName() {
		return dbName.get();
	}

	public void setDbName(String dbName) {
		this.dbName.set(dbName);
	}

	public String getDbUsername() {
		return dbUsername.get();
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername.set(dbUsername);
	}

	public String getDbPassword() {
		return dbPassword.get();
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword.set(dbPassword);
	}

}
