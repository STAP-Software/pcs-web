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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Configuration Entity class representing the CoarsePhasingOption database table.  
 * @author smichaels
 *
 */
@Entity
@Table(name = "CoarsePhasingOption")
@NamedQueries({
	@NamedQuery(name = "findCoarsePhasingOption", query = "SELECT o from CoarsePhasingOption o where o.coarseOptionId = :coarseOptionId" ),
	@NamedQuery(name = "findAllCoarsePhasingOptions", query = "SELECT o from CoarsePhasingOption o ORDER BY o.coarseOptionId" )	
})
public class CoarsePhasingOption {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long coarseOptionId;
	private String coarseOptionName;
	private int defaultGainNumber; 

	public CoarsePhasingOption() {
	
	}

	public Long getCoarseOptionId() {
		return coarseOptionId;
	}

	public void setCoarseOptionId(Long coarseOptionId) {
		this.coarseOptionId = coarseOptionId;
	}

	public String getCoarseOptionName() {
		return coarseOptionName;
	}

	public void setCoarseOptionName(String coarseOptionName) {
		this.coarseOptionName = coarseOptionName;
	}

	public int getDefaultGainNumber() {
		return defaultGainNumber;
	}

	public void setDefaultGainNumber(int defaultGainNumber) {
		this.defaultGainNumber = defaultGainNumber;
	}

}