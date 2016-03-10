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
	int defaultRefBeamNum;

	
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



	public int getDefaultRefBeamNum() {
		return defaultRefBeamNum;
	}

	public void setDefaultRefBeamNum(int defaultRefBeamNum) {
		this.defaultRefBeamNum = defaultRefBeamNum;
	}
	
	public boolean isNewRecord() {
		return sufsGroupId == null;
	}


}
