/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.instrument.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.tmt.aps.peas.common.FloatPoint;

/**
 * Configuration Entity class representing the SufsGroup table.  
 * @author smichaels
 */
@Entity
@Table(name = "SufsGroup")
@NamedQueries({ @NamedQuery(name = "findAllSufsGroups", query = "SELECT o from SufsGroup o") })
public class SufsGroup {

	@Id
	@SequenceGenerator(
		    name = "sufsGroup_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "sufsGroup_gen"
		)
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
