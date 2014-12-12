/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Entity
@Table(name = "FIConfigActual")
@PrimaryKeyJoinColumn(name="fiConfigId")
public class FIConfigActual extends FIConfig {

	public FIConfigActual() {
		super();
	}

	public FIConfigActual(FIConfig fiConfig) throws Exception {
		super(fiConfig);
	}

	@Transient
	RefBeamMap refDefMap;

	public RefBeamMap getRefDefMap() {
		return refDefMap;
	}

	public void setRefDefMap(RefBeamMap refDefMap) {
		this.refDefMap = refDefMap;
	}
	
	

}
