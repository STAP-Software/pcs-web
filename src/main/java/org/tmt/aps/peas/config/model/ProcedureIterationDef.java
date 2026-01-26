package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Configuration entity class representing the ProcedureIterationDef table
 * @author smichaels
 *
 */
@Entity
@Table(name = "ProcedureIterationDef")
@NamedQueries({
    @NamedQuery(
        name = "findIterationDefs",
        query = "SELECT pid FROM ProcedureIterationDef pid " +
                "INNER JOIN FETCH pid.procedureType " +  // no alias
                "ORDER BY pid.iterationEntityOrder"
    )
})

public class ProcedureIterationDef {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long procedureIterationDefId;

	String iterationEntityClassName;
	String iterationEntityAccessName;
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

	public String getIterationEntityAccessName() {
		return iterationEntityAccessName;
	}

	public void setIterationEntityAccessName(String iterationEntityAccessName) {
		this.iterationEntityAccessName = iterationEntityAccessName;
	}
	
}




