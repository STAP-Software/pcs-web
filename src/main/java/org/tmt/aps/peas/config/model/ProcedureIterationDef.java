package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Configuration entity class representing the ProcedureIterationDef table
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureIterationDef")
@NamedQueries({ @NamedQuery(name = "findIterationDefs", query = "SELECT pid from ProcedureIterationDef pid INNER JOIN FETCH pid.procedureType pt"
		+ " where pt.procedureTypeId = :procedureTypeId") })
public class ProcedureIterationDef {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long procedureIterationDefId;

	String iterationEntityClassName;
	int iterationEntityOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "procedureTypeId")
	ProcedureType procedureType;
	
	

	public Long getProcedureIterationDefId() {
		return procedureIterationDefId;
	}

	public void setProcedureIterationDefId(Long procedureIterationDefId) {
		this.procedureIterationDefId = procedureIterationDefId;
	}

	public String getIterationEntityClassName() {
		return iterationEntityClassName;
	}

	public void setIterationEntityClassName(String iterationEntityClassName) {
		this.iterationEntityClassName = iterationEntityClassName;
	}

	public int getIterationEntityOrder() {
		return iterationEntityOrder;
	}

	public void setIterationEntityOrder(int iterationEntityOrder) {
		this.iterationEntityOrder = iterationEntityOrder;
	}

	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}
	
}




