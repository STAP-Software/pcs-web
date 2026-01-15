/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

import org.tmt.aps.peas.instrument.model.CcdType;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.procedure.model.ProcedureType;

/**
 * Configuration entity class representing the IterationListConfigOption table.  This table is joined with the IterationListConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "IterationListConfigOption")
@PrimaryKeyJoinColumn(name="iterationListConfigId")
@NamedQueries({
	@NamedQuery(name = "findIterationListConfigOptions", query = "SELECT o from IterationListConfigOption o INNER JOIN FETCH o.procedureType p "
			+ "INNER JOIN FETCH o.instrument inst INNER JOIN FETCH o.ccdType t "
			+ "where p.procedureTypeId = :procedureTypeId AND inst.instrumentId = :instrumentId "
			+ "AND t.ccdTypeId = :ccdTypeId ORDER BY o.optionOrder" )
})
public class IterationListConfigOption extends IterationListConfig {
	
	int optionOrder;
	
	@ManyToOne
	@JoinColumn(name = "procedureTypeId")
	private ProcedureType procedureType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	private Instrument instrument;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ccdTypeId")
	private CcdType ccdType;

	
	public ProcedureType getProcedureType() {
		return procedureType;
	}

	public void setProcedureType(ProcedureType procedureType) {
		this.procedureType = procedureType;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public CcdType getCcdType() {
		return ccdType;
	}

	public void setCcdType(CcdType ccdType) {
		this.ccdType = ccdType;
	}

	public int getOptionOrder() {
		return optionOrder;
	}

	public void setOptionOrder(int optionOrder) {
		this.optionOrder = optionOrder;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof IterationListConfig) {
			IterationListConfig candidate = (IterationListConfig)obj;
			// if the iteration value list encoded are the same, these are the same
			
			return (candidate.getIterationValueListEncoded().equals(getIterationValueListEncoded()));
		}
		return super.equals(obj);
	}
	
}
