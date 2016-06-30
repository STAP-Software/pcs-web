package org.tmt.aps.peas.help.ui;

/**
 * Data bean class used in the help browser tree. 
 * @author smichaels
 */
public class HelpPageLink {

	private String name;
	private String link;

	
	public HelpPageLink(String name, String link) {
		this.name = name;

		this.link = link;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
