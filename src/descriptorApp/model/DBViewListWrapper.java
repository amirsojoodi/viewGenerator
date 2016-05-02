package descriptorApp.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dbViews")
public class DBViewListWrapper {

	private List<DBView> dbViews;

    @XmlElement(name = "dbView")
	public List<DBView> getDbViews() {
		return dbViews;
	}

	public void setDbViews(List<DBView> dbViews) {
		this.dbViews = dbViews;
	}
	
}