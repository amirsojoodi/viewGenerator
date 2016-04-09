package descriptorApp.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connections")
public class DBConnectionListWrapper {

	private List<DBConnection>	dbConnections;

    @XmlElement(name = "connection")
	public List<DBConnection> getDbConnections() {
		return dbConnections;
	}

	public void setDbConnections(List<DBConnection> dbConnections) {
		this.dbConnections = dbConnections;
	}
	
}