/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Configuration entity class representing the FrameCorrectionConfigDefaults table.  This table is joined with the FrameCorrectionConfig table using inheritance model.
 * @author smichaels
 */
@Entity
@Table(name = "FrameCorrectionConfigDefaults")
@PrimaryKeyJoinColumn(name="frameCorrectionConfigId")
@NamedQueries({
	@NamedQuery(name = "findFrameCorrectionConfigDefaults", query = "SELECT o from FrameCorrectionConfigDefaults o " )
})

public class FrameCorrectionConfigDefaults extends FrameCorrectionConfig {
		
}
