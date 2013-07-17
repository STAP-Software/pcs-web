package org.tmt.aps.peas.config.ui;

import java.io.Serializable;
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

	public List<Integer> getSelectedSpots() {
		return selectedSpots;
	}

	public void setSelectedSpots(List<Integer> selectedSpots) {
		this.selectedSpots = selectedSpots;
	}

	public Map<String, Integer> getSpots() {
		return spots;
	}

	@PostConstruct
	public void init() {

		try {
			String instrumentIdStr = peasProperties.getProp("org.tmt.aps.peas.instrumentId");
			String telescopeIdStr = peasProperties.getProp("org.tmt.aps.peas.telescopeId");

			spots = new LinkedHashMap<String, Integer>();
			for (int i = 1; i <= 160; i++) {
				spots.put("spot  # " + i, i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String doViewMissingSpots() {
		breadcrumbMenuBean.addFirstItem("Missing Spots Configuration", "doViewMissingSpots()");

		return "/modules/config/missingSpots.xhtml?faces-redirect=true";

	}

}
