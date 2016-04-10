package descriptorApp.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.controlsfx.dialog.Dialogs;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import descriptorApp.MainApp;

public class IOOperations {

	private DBConnection dbConnection;

	public static final String metaTableName = "MetaDescriptions";
	public static final String successfullyUpdatedMessage = "Successfully applied changes in DataBase!\n";
	private static final String connectionsFilePath = "connectionFile.xml";
	private static final String viewsFilePath = "viewFile.xml";

	private File viewsFile;
	private File connectionsFile;
	private MainApp mainApp;

	public IOOperations(MainApp mainApp) {
		this.mainApp = mainApp;
		dbConnection = new DBConnection();
		connectionsFile = new File(connectionsFilePath);
		viewsFile = new File(viewsFilePath);
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

		try {
			conn = ds.getConnection();
			String selectFromMetaDescriptions = "SELECT id, tableName, columnName, understandableName, description "
					+ "FROM " + metaTableName;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectFromMetaDescriptions);
			rs.close();
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
		return "Connection Tested Successfully!";
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
			Dialogs.create()
					.title("Error")
					.masthead(
							"Could not load data from file:\n"
									+ connectionsFile.getPath())
					.showException(e);
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
			Dialogs.create()
					.title("Error")
					.masthead(
							"Could not save data to file:\n"
									+ connectionsFile.getPath())
					.showException(e);
		}
	}

	public void loadViewDataFromFile() {
		try {
			JAXBContext context = JAXBContext
					.newInstance(DBViewListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			DBViewListWrapper wrapper = (DBViewListWrapper) um
					.unmarshal(viewsFile);

			mainApp.getViewData().clear();
			mainApp.getViewData().addAll(wrapper.getDbViews());

			// Save the file path to the registry.
			// setPersonFilePath(file);

		} catch (Exception e) { // catches ANY exception
			Dialogs.create()
					.title("Error")
					.masthead(
							"Could not load data from file:\n"
									+ viewsFile.getPath()).showException(e);
		}
	}

	public void saveViewDataToFile() {
		try {
			JAXBContext context = JAXBContext
					.newInstance(DBViewListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Wrapping our person data.
			DBViewListWrapper wrapper = new DBViewListWrapper();
			wrapper.setDbViews(mainApp.getViewData());

			// Marshalling and saving XML to the file.
			m.marshal(wrapper, viewsFile);

			// Save the file path to the registry.
			// setPersonFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Dialogs.create()
					.title("Error")
					.masthead(
							"Could not save data to file:\n"
									+ viewsFile.getPath()).showException(e);
		}
	}

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
