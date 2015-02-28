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

@Entity
@Table(name = "AutoCenterTelConfigDefaults")
@PrimaryKeyJoinColumn(name="autoCenterTelConfigId")
@NamedQueries({
	@NamedQuery(name = "findAutoCenterTelConfigDefaults", query = "SELECT o from AutoCenterTelConfigDefaults o" )
})
public class AutoCenterTelConfigDefaults extends AutoCenterTelConfig {
	
}
