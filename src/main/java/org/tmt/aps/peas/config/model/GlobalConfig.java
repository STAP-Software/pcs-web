/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.tmt.aps.peas.common.MessageGenerator;
import org.tmt.aps.peas.common.Point;

@Entity
@Table(name = "GlobalConfig")
@Inheritance(strategy=InheritanceType.JOINED)
public class GlobalConfig {

	@Transient
	Logger logger = Logger.getLogger(this.getClass());
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long globalConfigId;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date updateDate;
		
	int coarseMirrorX;
	int coarseMirrorY;
	int fineMirrorX;
	int fineMirrorY;

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


	public int getCoarseMirrorX() {
		return coarseMirrorX;
	}

	public void setCoarseMirrorX(int coarseMirrorX) {
		this.coarseMirrorX = coarseMirrorX;
	}

	public int getCoarseMirrorY() {
		return coarseMirrorY;
	}

	public void setCoarseMirrorY(int coarseMirrorY) {
		this.coarseMirrorY = coarseMirrorY;
	}

	public int getFineMirrorX() {
		return fineMirrorX;
	}

	public void setFineMirrorX(int fineMirrorX) {
		this.fineMirrorX = fineMirrorX;
	}

	public int getFineMirrorY() {
		return fineMirrorY;
	}

	public void setFineMirrorY(int fineMirrorY) {
		this.fineMirrorY = fineMirrorY;
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
	
	public Point getFineMirrorDefault() {
		return new Point((int)fineMirrorX, (int)fineMirrorY);
	}
}
