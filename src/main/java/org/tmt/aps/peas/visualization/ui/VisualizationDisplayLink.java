package org.tmt.aps.peas.visualization.ui;

public class VisualizationDisplayLink {

	private String name;
	private String onclick;

	
	public VisualizationDisplayLink(String name, String onclick) {
		this.name = name;

		this.onclick = onclick;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOnclick() {
		return onclick;
	}
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
	
}
