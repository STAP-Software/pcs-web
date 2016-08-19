/**
 * @author Scott Michaels
 * Copyright (C) 2014 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Configuration entity class representing the IterationListConfig table
 * @author smichaels
 *
 */
@Entity
@Table(name = "IterationListConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class IterationListConfig {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long iterationListConfigId;

	String iterationValueListEncoded;
	int iterationEntityOrder;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureIterationDefId")
	ProcedureIterationDef procedureIterationDef;

	// procedureId bigint,    FIXME: we need to remove this from the database, we attach to procedures through the procedureConfigSet

	
	
	public IterationListConfig() {
		
	}
	
	public IterationListConfig(IterationListConfig source) throws Exception {
		
		BeanUtils.copyProperties(this, source);

		this.iterationListConfigId = null;
	}


	public Long getIterationListConfigId() {
		return iterationListConfigId;
	}

	public void setIterationListConfigId(Long iterationListConfigId) {
		this.iterationListConfigId = iterationListConfigId;
	}

	public String getIterationValueListEncoded() {
		return iterationValueListEncoded;
	}

	public void setIterationValueListEncoded(String iterationValueListEncoded) {
		this.iterationValueListEncoded = iterationValueListEncoded;
	}

	public int getIterationEntityOrder() {
		return iterationEntityOrder;
	}

	public void setIterationEntityOrder(int iterationEntityOrder) {
		this.iterationEntityOrder = iterationEntityOrder;
	}

	public ProcedureIterationDef getProcedureIterationDef() {
		return procedureIterationDef;
	}

	public void setProcedureIterationDef(ProcedureIterationDef procedureIterationDef) {
		this.procedureIterationDef = procedureIterationDef;
	}

}








	