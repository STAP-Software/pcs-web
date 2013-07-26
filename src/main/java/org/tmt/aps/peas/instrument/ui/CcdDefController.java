package org.tmt.aps.peas.instrument.ui;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.tmt.aps.peas.BreadcrumbMenuBean;
import org.tmt.aps.peas.instrument.business.CcdDefMgmt;
import org.tmt.aps.peas.instrument.model.Ccd;
import org.tmt.aps.peas.instrument.model.Instrument;

@Named
@SessionScoped
public class CcdDefController implements Serializable {

	@EJB
	CcdDefMgmt ccdDefMgmt;

	@Inject
	private BreadcrumbMenuBean breadcrumbMenuBean;

	private List<Ccd> ccdList;
	private Ccd ccd;
	private Integer xHotPixel;  // FIXME: these should be deprecated and use the hotPixel Point
	private Integer yHotPixel;
	private Point hotPixel;

	@PostConstruct
	private void init() {
		refreshCcdList();
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


	public Integer getxHotPixel() {
		return xHotPixel;
	}

	public void setxHotPixel(Integer xHotPixel) {
		this.xHotPixel = xHotPixel;
	}

	public Integer getyHotPixel() {
		return yHotPixel;
	}

	public void setyHotPixel(Integer yHotPixel) {
		this.yHotPixel = yHotPixel;
	}

	public Point getHotPixel() {
		return hotPixel;
	}

	public void setHotPixel(Point hotPixel) {
		this.hotPixel = hotPixel;
	}

	private void refreshCcdList() {
		ccdList = ccdDefMgmt.findAllCcds();
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

		ccd = new Ccd();

		breadcrumbMenuBean.addItem("New Ccd", "doNewCcd()");

		return "/modules/sysadmin/ccdDetail.xhtml?faces-redirect=true";

	}

	public void doDeleteHotPixel() {

		ccd.removeHotPixel(hotPixel);
		
		ccdDefMgmt.updateCcd(ccd);

	}

	public String doSaveCcd() {

		ccdDefMgmt.createCcd(ccd);

		refreshCcdList();

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

	}

	public String doCancelSaveCcd() {

		breadcrumbMenuBean.addFirstItem("PCS CCDs", "doViewCcdList()");

		return "/modules/sysadmin/ccdList.xhtml?faces-redirect=true";

	}

	public void doAddHotPixel() {


		ccd.addHotPixel(new Point(xHotPixel, yHotPixel));
		
		ccdDefMgmt.updateCcd(ccd);
		
		xHotPixel = null;
		yHotPixel = null;

	}

}
