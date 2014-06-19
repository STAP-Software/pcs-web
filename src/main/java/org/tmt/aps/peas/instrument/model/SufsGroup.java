/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.common.FloatPoint;

@Entity
@Table(name = "SufsGroup")
@NamedQueries({ @NamedQuery(name = "findAllSufsGroups", query = "SELECT o from SufsGroup o") })
public class SufsGroup {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long sufsGroupId;

	int groupNumber;
	float coarseMirrorPosX;
	float coarseMirrorPosY;
	float telPosAz;
	float telPosEl;
	int defaultRefBeamNum;
	
	@ManyToOne
	@JoinColumn (name="pupilMaskId")
	private PupilMask pupilMask;

	
	public Long getSufsGroupId() {
		return sufsGroupId;
	}

	public void setSufsGroupId(Long sufsGroupId) {
		this.sufsGroupId = sufsGroupId;
	}

	public int getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(int groupNumber) {
		this.groupNumber = groupNumber;
	}

	public float getCoarseMirrorPosX() {
		return coarseMirrorPosX;
	}

	public void setCoarseMirrorPosX(float coarseMirrorPosX) {
		this.coarseMirrorPosX = coarseMirrorPosX;
	}

	public float getCoarseMirrorPosY() {
		return coarseMirrorPosY;
	}

	public void setCoarseMirrorPosY(float coarseMirrorPosY) {
		this.coarseMirrorPosY = coarseMirrorPosY;
	}

	public FloatPoint getCoarseMirrorPos() {
		return new FloatPoint(coarseMirrorPosX, coarseMirrorPosY);
	}

	public void setCoarseMirrorPos(FloatPoint coarseMirrorPos) {
		this.coarseMirrorPosX = coarseMirrorPos.getX();
		this.coarseMirrorPosY = coarseMirrorPos.getY();
	}

	public float getTelPosAz() {
		return telPosAz;
	}

	public void setTelPosAz(float telPosAz) {
		this.telPosAz = telPosAz;
	}

	public float getTelPosEl() {
		return telPosEl;
	}

	public void setTelPosEl(float telPosEl) {
		this.telPosEl = telPosEl;
	}

	public int getDefaultRefBeamNum() {
		return defaultRefBeamNum;
	}

	public void setDefaultRefBeamNum(int defaultRefBeamNum) {
		this.defaultRefBeamNum = defaultRefBeamNum;
	}

	public PupilMask getPupilMask() {
		return pupilMask;
	}

	public void setPupilMask(PupilMask pupilMask) {
		this.pupilMask = pupilMask;
	}

	
	public boolean isNewRecord() {
		return sufsGroupId == null;
	}


}
