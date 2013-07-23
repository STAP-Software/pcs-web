package org.tmt.aps.peas.instrument.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.Instrument;

@Named
@SessionScoped
public class CcdDefController implements Serializable {


	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Ccd> ccdList;
	private Ccd ccd;
	
	@PostConstruct
	private void init() {
		ccdList = new ArrayList<Ccd>();
		
		Ccd ccd = new Ccd("ccdName1", "First CCD used on K1", "356,234,678,994", new Instrument("PCS 1"));
		ccdList.add(ccd);
		ccd = new Ccd("ccdName2", "Another CCD used on K1", "367,869,758,372,287,943", null);
		ccdList.add(ccd);
		ccd = new Ccd("ccdName3", "Spare CCD we never used", "367,869,758,372,287,943", null);
		ccdList.add(ccd);
		ccd = new Ccd("ccdName4", "Spare CCD from Lab", "367,869,758,372,287,943", null);
		ccdList.add(ccd);
		ccd = new Ccd("ccdName5", "Current Keck 2 CCD", "367,869,758,372,287,943", new Instrument("PCS 2"));
		ccdList.add(ccd);
		ccd = new Ccd("ccdName6", "Spare CCD #3", "367,869,758,372,287,943", null);
		ccdList.add(ccd);
	}
	

	public List<Ccd> getCcdList() {
		return ccdList;
	}


	public void setCcdList(List<Ccd> ccdList) {
		this.ccdList = ccdList;
	}


	public Ccd getCcd() {
		return ccd;
	}


	public void setCcd(Ccd ccd) {
		this.ccd = ccd;
	}


	public String doViewCcdList() {

		
		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";
	}
	
	public String doViewCcd() {

		
		breadcrumbMenuBean.addItem(ccd.getCcdName(), "doViewCcd()");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";
	}
	
	public String doNewCcd() {

		breadcrumbMenuBean.addItem("New Ccd", "doNewCcd()");

		return "/modules/sysadmin/ccdHotPixel.xhtml?faces-redirect=true";

	}
	
	public void doDeleteHotPixel() {

		// TODO: remove the hot pixel from the list and save
		
	}
	
	public void doSaveCcdDef() {

		// TODO: remove the hot pixel from the list and save
		
	}
	
	public String doCancelSaveCcdDef() {

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";		
		
	}
	
	public String doNewCcdDef() {

		breadcrumbMenuBean.addItem("New CCD", "doNewCcdDef()");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";

	}
	
	public String doNewHotPixel() {

		breadcrumbMenuBean.addItem("New Hot Pixel", "doHotPixel()");

		return "/modules/sysadmin/ccdHotPixel.xhtml?faces-redirect=true";

	}
	




}
