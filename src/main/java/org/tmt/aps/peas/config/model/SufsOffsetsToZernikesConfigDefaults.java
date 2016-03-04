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
@Table(name = "SufsOffsetsToZernikesConfigDefaults")
@PrimaryKeyJoinColumn(name="SufsOffsetsToZernikesConfigId")
@NamedQueries({ @NamedQuery(name = "findSufsOffsetsToZernikesConfig", query = "SELECT o from SufsOffsetsToZernikesConfigDefaults o") })
public class SufsOffsetsToZernikesConfigDefaults extends SufsOffsetsToZernikesConfig {

	
}
