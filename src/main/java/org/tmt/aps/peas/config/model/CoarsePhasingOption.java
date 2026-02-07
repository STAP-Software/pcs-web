/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

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
	@SequenceGenerator(
		    name = "coarseOption_gen",
		    sequenceName = "hibernate_sequence",
		    allocationSize = 1
		)
	@GeneratedValue(
		    strategy = GenerationType.SEQUENCE,
		    generator = "coarseOption_gen"
		)

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