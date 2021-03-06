package descriptorApp.model;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import descriptorApp.MainApp;

public class IOOperations {

	private DBConnection dbConnection;

	public static final String metaTableName = "MetaDescriptions";
	public static final String successfullyUpdatedMessage = "Successfully applied changes in DataBase!\n";
	public static final String connectionsFilePath = "connectionFile.xml";
	public static final String viewsFilePath = "viewFile.xml";
	public static final String connectionTestSuccessfullMessage = "Connection Tested Successfully!";
	public static final String connectionTestUnsuccessfullMessage = "Connection is not Valid!";

	// private File viewsFile;
	private File connectionsFile;
	private MainApp mainApp;

	public IOOperations(MainApp mainApp) {
		this.mainApp = mainApp;
		dbConnection = new DBConnection();
		connectionsFile = new File(connectionsFilePath);
	}

	public void getInitialDescriptionsFromDB(
			HashMap<String, ArrayList<String>> tablesAndColumns) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = null;
			int id;
			String columnName, tableName, understandableName, description;

			String selectFromMetaDescriptions = "SELECT id, tableName, columnName, understandableName, description "
					+ "FROM " + metaTableName;

			String createDescriptionTableQuery = "CREATE TABLE "
					+ metaTableName + " " + "(id INTEGER not NULL, "
					+ " tableName VARCHAR(255), "
					+ " columnName VARCHAR(255), "
					+ " understandableName NVARCHAR(255), "
					+ " description NVARCHAR(255), " + " PRIMARY KEY ( id ))";

			// System.out.println(query);

			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, "MetaDescriptions", null);
			if (rs.next()) {
				// System.out.println("Table MetaDescriptions exists.");

				rs.close();
				rs = stmt.executeQuery(selectFromMetaDescriptions);

				while (rs.next()) {
					id = rs.getInt(1);
					tableName = rs.getString(2);
					columnName = rs.getString(3);
					understandableName = (rs.getString(4) == null) ? "" : rs
							.getString(4);
					description = (rs.getString(5) == null) ? "" : rs
							.getString(5);

					Description tmpDescription = new Description(id, tableName,
							columnName, understandableName, description);

					tmpDescription.setDescriptionID(id);

					insertIntoMap(tmpDescription, tablesAndColumns);
					mainApp.getDescriptionData().add(tmpDescription);
					// System.out.println(tableName + " : " + columnName);
				}
				Description.hasAnyChanged.setValue(false);

			} else {
				stmt.executeUpdate(createDescriptionTableQuery);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadAllColumnsFromDB(
			HashMap<String, ArrayList<String>> tablesAndColumns,
			boolean addToAll) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			String columnName, tableName;

			String findAllTablesAndColumnsQuery = "SELECT o.Name, c.Name "
					+ "FROM sys.columns c "
					+ "JOIN sys.objects o ON o.object_id = c.object_id "
					+ "WHERE o.type = 'U'"/* + "ORDER BY o.Name " */;

			rs = stmt.executeQuery(findAllTablesAndColumnsQuery);
			ArrayList<String> columnsOfTable = null;
			while (rs.next()) {

				tableName = rs.getString(1);
				columnName = rs.getString(2);

				if (!tablesAndColumns.containsKey(tableName)) {
					tablesAndColumns.put(tableName, new ArrayList<String>());
				}

				columnsOfTable = tablesAndColumns.get(tableName);
				if (!columnsOfTable.contains(columnName)) {
					columnsOfTable.add(columnName);

					if (addToAll) {
						Description tmpDescription = new Description(tableName,
								columnName);
						tmpDescription.setHasChanged(true);
						Description.hasAnyChanged.setValue(true);
						mainApp.getDescriptionData().add(tmpDescription);
					}
					// System.out.println(tableName + " : " + columnName);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void insertIntoMap(Description tmpDescription,
			HashMap<String, ArrayList<String>> tablesAndColumns) {
		if (!tablesAndColumns.containsKey(tmpDescription.getTableName())) {
			ArrayList<String> columns = new ArrayList<String>();
			columns.add(tmpDescription.getColumnName());
			tablesAndColumns.put(tmpDescription.getTableName(), columns);
		} else {
			ArrayList<String> columns = tablesAndColumns.get(tmpDescription
					.getTableName());
			if (!columns.contains(tmpDescription.getColumnName())) {
				columns.add(tmpDescription.getColumnName());
			}
		}
	}

	public String checkConnection() {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		boolean reachable;

		try {
			conn = ds.getConnection();

			reachable = conn.isValid(10);// 10 sec

			// String selectFromMetaDescriptions =
			// "SELECT id, tableName, columnName, understandableName, description "
			// + "FROM " + metaTableName;
			// stmt = conn.createStatement();
			// rs = stmt.executeQuery(selectFromMetaDescriptions);
			// rs.close();
		} catch (SQLServerException e) {
			return e.getMessage();
		} catch (SQLException e) {
			return e.getMessage();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					return e.getMessage();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
					return e.getMessage();
				}
			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					return e.getMessage();
				}
		}
		if (reachable) {
			return connectionTestSuccessfullMessage;
		}
		return connectionTestUnsuccessfullMessage;
	}

	public String updateDescriptionsInDB(List<Description> descriptions) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String message = "";

		if (descriptions.size() == 0) {
			return successfullyUpdatedMessage;
		}

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = null;

			for (Description tmpDescription : descriptions) {
				String query = "UPDATE " + metaTableName + " SET tableName = '"
						+ tmpDescription.getTableName() + "' , columnName = '"
						+ tmpDescription.getColumnName()
						+ "' , understandableName = N'"
						+ tmpDescription.getUnderstandableName()
						+ "' , description = N'"
						+ tmpDescription.getDescription() + "' WHERE id = "
						+ tmpDescription.getDescriptionID();
				// System.out.println(query);
				if (stmt.executeUpdate(query) == 0) {
					query = "INSERT INTO " + metaTableName + " VALUES ("
							+ tmpDescription.getDescriptionID() + " , '"
							+ tmpDescription.getTableName() + "' , '"
							+ tmpDescription.getColumnName() + "' , N'"
							+ tmpDescription.getUnderstandableName() + "' , N'"
							+ tmpDescription.getDescription() + "')";
					System.out.println(stmt.executeUpdate(query));
				}
			}
			message += successfullyUpdatedMessage;

		} catch (SQLException e) {
			e.printStackTrace();
			message += e.getMessage();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					message += e.getMessage();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					message += e.getMessage();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					message += e.getMessage();
				}
			}
		}
		return message;
	}

	public String deleteDescriptionsInDB(List<Description> descriptions) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String message = "";

		if (descriptions.size() == 0) {
			return successfullyUpdatedMessage;
		}

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			rs = null;

			for (Description tmpDescription : descriptions) {
				String query = "DELETE FROM " + metaTableName + " WHERE ID = "
						+ tmpDescription.getDescriptionID();
				// System.out.println(query);
				stmt.executeUpdate(query);
			}
			message += successfullyUpdatedMessage;

		} catch (SQLException e) {
			message += e.getMessage();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					message += e.getMessage();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					message += e.getMessage();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					message += e.getMessage();
				}
			}
		}
		return message;
	}

	public void loadConnectionDataFromFile() {
		try {
			JAXBContext context = JAXBContext
					.newInstance(DBConnectionListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			DBConnectionListWrapper wrapper = (DBConnectionListWrapper) um
					.unmarshal(connectionsFile);

			mainApp.getConnectionData().clear();
			mainApp.getConnectionData().addAll(wrapper.getDbConnections());

			// Save the file path to the registry.
			// setPersonFilePath(file);

		} catch (Exception e) { // catches ANY exception
			showException(e, "Error", "Could not save data to file!",
					connectionsFile.getPath());
		}
	}

	public void saveConnectionDataToFile() {
		try {
			JAXBContext context = JAXBContext
					.newInstance(DBConnectionListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			DBConnectionListWrapper wrapper = new DBConnectionListWrapper();
			wrapper.setDbConnections(mainApp.getConnectionData());

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, connectionsFile);

			// Save the file path to the registry.
			// setPersonFilePath(file);
		} catch (Exception e) { // catches ANY exception
			showException(e, "Error", "Could not save data to file!",
					connectionsFile.getPath());
		}
	}

	public void showException(Exception e, String title, String body,
			String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(body);
		alert.setContentText(content);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	public String[][] previewView(String queryString, int numberOfRows) {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData rsMetaData = null;
		String[][] results = null;

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = stmt.executeQuery(queryString);
			rsMetaData = rs.getMetaData();
			
			rs.last();

			int rowCount = rs.getRow();
			rowCount = Math.min(numberOfRows, rowCount);
			results = new String[rowCount + 1][rsMetaData.getColumnCount()];

			rs.beforeFirst();
			
			for (int i = 0; i < rsMetaData.getColumnCount(); i++) {
				results[0][i] = rsMetaData.getColumnLabel(i + 1);
			}
			int i = 1;
			while (rs.next() && i <= rowCount) {
				for (int j = 0; j < rsMetaData.getColumnCount(); j++) {
					results[i][j] = rs.getString(j + 1);
				}
				i++;
			}

		} catch (SQLException e) {
			showException(e, "Error", "Could not execute the query!",
					queryString);
			return null;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return results;
	}

	public boolean createView(String queryString) {

		boolean exceptionOccurred = false;

		Connection conn = null;
		Statement stmt = null;

		SQLServerDataSource ds = new SQLServerDataSource();
		ds.setServerName(dbConnection.getServerIP());
		ds.setPortNumber(Integer.parseInt(dbConnection.getServerPort()));
		ds.setDatabaseName(dbConnection.getDbName());
		ds.setUser(dbConnection.getDbUsername());
		ds.setPassword(dbConnection.getDbPassword());

		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();

			stmt.executeUpdate(queryString);

		} catch (SQLException e) {
			showException(e, "Error", "Could not execute the query!",
					queryString);
			exceptionOccurred = true;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return exceptionOccurred;
	}

	/*
	 * public void loadViewDataFromFile() { try { JAXBContext context =
	 * JAXBContext .newInstance(DBViewListWrapper.class); Unmarshaller um =
	 * context.createUnmarshaller();
	 * 
	 * // Reading XML from the file and unmarshalling. viewsFile = new
	 * File(mainApp.getActiveConnection() .getConnectionName() + "-" +
	 * viewsFilePath);
	 * 
	 * if (viewsFile.exists()) { DBViewListWrapper wrapper = (DBViewListWrapper)
	 * um .unmarshal(viewsFile);
	 * 
	 * mainApp.getViewData().clear();
	 * 
	 * if (wrapper != null && wrapper.getDbViews() != null) {
	 * mainApp.getViewData().addAll(wrapper.getDbViews()); } } else {
	 * mainApp.getViewData().clear(); } // Save the file path to the registry.
	 * // setPersonFilePath(file);
	 * 
	 * } catch (Exception e) { // catches ANY exception e.printStackTrace();
	 * Dialogs.create().title("Error")
	 * .masthead("Could not load data from view file!\n") .showException(e); } }
	 */
	/*
	 * public void saveViewDataToFile() { try { JAXBContext context =
	 * JAXBContext .newInstance(DBViewListWrapper.class); Marshaller m =
	 * context.createMarshaller();
	 * m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	 * 
	 * // Wrapping our person data. DBViewListWrapper wrapper = new
	 * DBViewListWrapper(); wrapper.setDbViews(mainApp.getViewData());
	 * 
	 * // Marshalling and saving XML to the file. viewsFile = new
	 * File(mainApp.getActiveConnection() .getConnectionName() + "-" +
	 * viewsFilePath); m.marshal(wrapper, viewsFile);
	 * 
	 * // Save the file path to the registry. // setPersonFilePath(file); }
	 * catch (Exception e) { // catches ANY exception Dialogs.create()
	 * .title("Error") .masthead( "Could not save data to file:\n" +
	 * viewsFile.getPath()).showException(e); } }
	 */

	public String getServerIP() {
		return dbConnection.getServerIP();
	}

	public void setServerIP(String serverIP) {
		dbConnection.setServerIP(serverIP);
	}

	public String getServerPort() {
		return dbConnection.getServerPort();
	}

	public void setServerPort(String serverPort) {
		dbConnection.setServerPort(serverPort);
	}

	public String getDbName() {
		return dbConnection.getDbName();
	}

	public void setDbName(String dbName) {
		dbConnection.setDbName(dbName);
	}

	public String getDbUsername() {
		return dbConnection.getDbUsername();
	}

	public void setDbUsername(String dbUsername) {
		dbConnection.setDbUsername(dbUsername);
	}

	public String getDbPassword() {
		return dbConnection.getDbPassword();
	}

	public void setDbPassword(String dbPassword) {
		dbConnection.setDbPassword(dbPassword);
	}

	public MainApp getMainApp() {
		return mainApp;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	public String getMetaTableName() {
		return metaTableName;
	}

}
