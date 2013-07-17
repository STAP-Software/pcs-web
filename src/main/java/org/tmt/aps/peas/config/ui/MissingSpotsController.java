package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.PeasProperties;
import org.tmt.aps.peas.config.business.GlobalConfigMgmt;
import org.tmt.aps.peas.config.model.Subimage;

@Named
@SessionScoped
public class MissingSpotsController implements Serializable {

	@EJB
	GlobalConfigMgmt globalConfigMgmt;
	@EJB
	PeasProperties peasProperties;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Integer> selectedSpots;

	private Map<String, Integer> spots;
	
	private List<Subimage> subimageDefList;
	
	String centroidNumbers; // for javascript svg display
	String centroidXs; // for javascript svg display
	String centroidYs; // for javascript svg display
	String missingSpots; // for javascript svg display



	@PostConstruct
	public void init() {

		try {
			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			spots = new LinkedHashMap<String, Integer>();
			for (int i = 1; i <= 160; i++) {
				spots.put("spot  # " + i, i);
			}
			
			// TODO: read in subimageDefList
			subimageDefList = new ArrayList<Subimage>();
			for (int i = 0; i < Subimage.CPH_DEF_X_ARRAY.length; i++) {
				Subimage subimage = new Subimage(i+1, Subimage.CPH_DEF_X_ARRAY[i], Subimage.CPH_DEF_Y_ARRAY[i]);
				subimageDefList.add(subimage);
			}
			
			// TODO: coarse bad spots are: 4, 11, 37, 38, 39, 40, 41, 42
			missingSpots = "4,11,37,38,39,40,41,42";
			
			// generate centroid numbers, x and y positions
			StringBuffer numBuf = new StringBuffer();
			StringBuffer xBuf = new StringBuffer();
			StringBuffer yBuf = new StringBuffer();
			for (Subimage subimage : subimageDefList) {
				numBuf.append(subimage.getSubimageNumber() + ",");
				xBuf.append(subimage.getxCcd() + ",");
				yBuf.append(subimage.getyCcd() + ",");
			}
			numBuf.deleteCharAt(numBuf.length()-1);
			xBuf.deleteCharAt(xBuf.length()-1);
			yBuf.deleteCharAt(yBuf.length()-1);
			centroidNumbers = numBuf.toString();
			centroidXs = xBuf.toString();
			centroidYs = yBuf.toString();
			System.out.println("centroidNumbers = " + centroidNumbers);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Integer> getSelectedSpots() {
		return selectedSpots;
	}

	public void setSelectedSpots(List<Integer> selectedSpots) {
		this.selectedSpots = selectedSpots;
	}

	public Map<String, Integer> getSpots() {
		return spots;
	}
	
	public String getCentroidNumbers() {
		return centroidNumbers;
	}

	public void setCentroidNumbers(String centroidNumbers) {
		this.centroidNumbers = centroidNumbers;
	}

	public String getCentroidXs() {
		return centroidXs;
	}

	public void setCentroidXs(String centroidXs) {
		this.centroidXs = centroidXs;
	}

	public String getCentroidYs() {
		return centroidYs;
	}

	public void setCentroidYs(String centroidYs) {
		this.centroidYs = centroidYs;
	}

	public String getMissingSpots() {
		return missingSpots;
	}

	public void setMissingSpots(String missingSpots) {
		this.missingSpots = missingSpots;
	}

	public String doViewMissingSpots() {
		breadcrumbMenuBean.addFirstItem("Missing Spots Configuration", "doViewMissingSpots()");

		return "/modules/config/missingSpots.xhtml?faces-redirect=true";

	}

}
