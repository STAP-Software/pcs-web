/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

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
