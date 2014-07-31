/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "FindCentConfig")
@NamedQueries({
	@NamedQuery(name = "findByMaskType", query = "SELECT o from FindCentConfig o INNER JOIN FETCH o.pupilMaskType p "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId" )
})
public class FindCentConfig {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long findCentConfigId;
	
	private int irad;
	private int imargin;
	private int ngauss;
	private int itermax;

	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;


	public Long getFindCentConfigId() {
		return findCentConfigId;
	}


	public void setFindCentConfigId(Long findCentConfigId) {
		this.findCentConfigId = findCentConfigId;
	}


	public int getIrad() {
		return irad;
	}


	public void setIrad(int irad) {
		this.irad = irad;
	}


	public int getImargin() {
		return imargin;
	}


	public void setImargin(int imargin) {
		this.imargin = imargin;
	}

	public int getNgauss() {
		return ngauss;
	}


	public void setNgauss(int ngauss) {
		this.ngauss = ngauss;
	}


	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}


	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}


	public int getItermax() {
		return itermax;
	}


	public void setItermax(int itermax) {
		this.itermax = itermax;
	}

	public String toString() {
		
		StringBuffer buf = new StringBuffer();
		buf.append("FindCentConfig:");
		buf.append("\nPupilMaskType = " + pupilMaskType.getPupilMaskTypeName());
		buf.append("\nirad = " + irad);
		buf.append("\nimargin = " + imargin);
		buf.append("\nngauss = " + ngauss);
		buf.append("\nitermax = " + itermax);
		buf.append("\n");

		return buf.toString();
	}
}
