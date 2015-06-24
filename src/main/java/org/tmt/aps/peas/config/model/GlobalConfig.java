/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;
import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "GlobalConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class GlobalConfig {

	Logger logger = Logger.getLogger(this.getClass());
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long globalConfigId;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date updateDate;
		
	float coarseMirrorX;
	float coarseMirrorY;

	boolean removeBadPixels;

	// TODO: should be in advanced SUFS
	int autoPointTelescope;
	
	private boolean autoDisplayCentroids;
	private boolean autoDisplayCentroidOffsets;
	private boolean autoDisplayAvgCentroidOffsets;
	private boolean autoDisplayActuatorDeltas;
	private boolean autoDisplayProcedureDataLog;


	public GlobalConfig() {
		
	}
	
	public GlobalConfig(GlobalConfig source) {
		
		try {
			BeanUtils.copyProperties(this, source);

			this.globalConfigId = null;
			
		} catch (Exception e) {
			logger.error(MessageGenerator.generateMessage("generic.error"), e);
		}
	}

	public float getCoarseMirrorX() {
		return coarseMirrorX;
	}

	public void setCoarseMirrorX(float coarseMirrorX) {
		this.coarseMirrorX = coarseMirrorX;
	}

	public float getCoarseMirrorY() {
		return coarseMirrorY;
	}

	public void setCoarseMirrorY(float coarseMirrorY) {
		this.coarseMirrorY = coarseMirrorY;
	}

	public boolean isRemoveBadPixels() {
		return removeBadPixels;
	}

	public void setRemoveBadPixels(boolean removeBadPixels) {
		this.removeBadPixels = removeBadPixels;
	}


	public int getAutoPointTelescope() {
		return autoPointTelescope;
	}

	public void setAutoPointTelescope(int autoPointTelescope) {
		this.autoPointTelescope = autoPointTelescope;
	}

	public Long getGlobalConfigId() {
		return globalConfigId;
	}

	public void setGlobalConfigId(Long globalConfigId) {
		this.globalConfigId = globalConfigId;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public boolean isAutoDisplayCentroids() {
		return autoDisplayCentroids;
	}

	public void setAutoDisplayCentroids(boolean autoDisplayCentroids) {
		this.autoDisplayCentroids = autoDisplayCentroids;
	}

	public boolean isAutoDisplayCentroidOffsets() {
		return autoDisplayCentroidOffsets;
	}

	public void setAutoDisplayCentroidOffsets(boolean autoDisplayCentroidOffsets) {
		this.autoDisplayCentroidOffsets = autoDisplayCentroidOffsets;
	}

	public boolean isAutoDisplayAvgCentroidOffsets() {
		return autoDisplayAvgCentroidOffsets;
	}

	public void setAutoDisplayAvgCentroidOffsets(boolean autoDisplayAvgCentroidOffsets) {
		this.autoDisplayAvgCentroidOffsets = autoDisplayAvgCentroidOffsets;
	}

	public boolean isAutoDisplayActuatorDeltas() {
		return autoDisplayActuatorDeltas;
	}

	public void setAutoDisplayActuatorDeltas(boolean autoDisplayActuatorDeltas) {
		this.autoDisplayActuatorDeltas = autoDisplayActuatorDeltas;
	}

	public boolean isAutoDisplayProcedureDataLog() {
		return autoDisplayProcedureDataLog;
	}

	public void setAutoDisplayProcedureDataLog(boolean autoDisplayProcedureDataLog) {
		this.autoDisplayProcedureDataLog = autoDisplayProcedureDataLog;
	}

	public Point getCoarseMirrorDefault() {
		return new Point((int)coarseMirrorX, (int)coarseMirrorY);
	}
}
