/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.refBeamMap.model.RefBeamMap;

@Entity
@Table(name = "FIConfig")
@NamedQueries({ @NamedQuery(name = "findByMaskTypeAndInstrument", query = "SELECT o from FIConfig o INNER JOIN FETCH o.pupilMaskType p INNER JOIN FETCH o.instrument i "
		+ "where p.pupilMaskTypeId = :pupilMaskTypeId and i.instrumentId = :instrumentId and o.lightSource = :lightSource") })
public class FIConfig {

	
	public static final int FORCE_SOURCE_REF_MAP = 1;
	public static final int FORCE_SOURCE_USER_ENTERED = 2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long fiConfigId;

	private int lightSource;

	private float uEst;
	private float uDelta0;

	private int matchbox;
	private int nThresh0;
	private float matchFineThresh;
	private int nPeakMinThresh;
	private int nPeakMaxThresh;
	private int lensletOrientation;
	private int spiralRingCount;

	private boolean forceScaleDefault;
	private boolean forceRotationDefault;

	@ManyToOne
	@JoinColumn(name = "pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn(name = "instrumentId")
	private Instrument instrument;

	public Long getFiConfigId() {
		return fiConfigId;
	}

	public void setFiConfigId(Long fiConfigId) {
		this.fiConfigId = fiConfigId;
	}

	public float getuEst() {
		return uEst;
	}

	public void setuEst(float uEst) {
		this.uEst = uEst;
	}

	public float getuDelta0() {
		return uDelta0;
	}

	public void setuDelta0(float uDelta0) {
		this.uDelta0 = uDelta0;
	}

	public int getMatchbox() {
		return matchbox;
	}

	public void setMatchbox(int matchbox) {
		this.matchbox = matchbox;
	}

	public int getnThresh0() {
		return nThresh0;
	}

	public void setnThresh0(int nThresh0) {
		this.nThresh0 = nThresh0;
	}

	public float getMatchFineThresh() {
		return matchFineThresh;
	}

	public void setMatchFineThresh(float matchFineThresh) {
		this.matchFineThresh = matchFineThresh;
	}

	public int getnPeakMinThresh() {
		return nPeakMinThresh;
	}

	public void setnPeakMinThresh(int nPeakMinThresh) {
		this.nPeakMinThresh = nPeakMinThresh;
	}

	public int getnPeakMaxThresh() {
		return nPeakMaxThresh;
	}

	public void setnPeakMaxThresh(int nPeakMaxThresh) {
		this.nPeakMaxThresh = nPeakMaxThresh;
	}

	public int getLensletOrientation() {
		return lensletOrientation;
	}

	public void setLensletOrientation(int lensletOrientation) {
		this.lensletOrientation = lensletOrientation;
	}

	public int getSpiralRingCount() {
		return spiralRingCount;
	}

	public void setSpiralRingCount(int spiralRingCount) {
		this.spiralRingCount = spiralRingCount;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public int getLightSource() {
		return lightSource;
	}

	public void setLightSource(int lightSource) {
		this.lightSource = lightSource;
	}

	public boolean isForceScaleDefault() {
		return forceScaleDefault;
	}

	public void setForceScaleDefault(boolean forceScaleDefault) {
		this.forceScaleDefault = forceScaleDefault;
	}

	public boolean isForceRotationDefault() {
		return forceRotationDefault;
	}

	public void setForceRotationDefault(boolean forceRotationDefault) {
		this.forceRotationDefault = forceRotationDefault;
	}

	@Transient
	private boolean forceScale;

	public boolean isForceScale() {
		return forceScale;
	}

	public void setForceScale(boolean forceScale) {
		this.forceScale = forceScale;
	}

	@Transient
	private boolean forceRotation;

	public boolean isForceRotation() {
		return forceRotation;
	}

	public void setForceRotation(boolean forceRotation) {
		this.forceRotation = forceRotation;
	}
	
	@Transient 
	private int forceScaleSource;
	
	public int getForceScaleSource() {
		return forceScaleSource;
	}

	public void setForceScaleSource(int forceScaleSource) {
		this.forceScaleSource = forceScaleSource;
	}

	@Transient
	private int forceRotationSource;
	
	public int getForceRotationSource() {
		return forceRotationSource;
	}

	public void setForceRotationSource(int forceRotationSource) {
		this.forceRotationSource = forceRotationSource;
	}

	@Transient
	private float forceScaleValue;
	
	public float getForceScaleValue() {
		return forceScaleValue;
	}

	public void setForceScaleValue(float forceScaleValue) {
		this.forceScaleValue = forceScaleValue;
	}

	@Transient 
	private float forceRotationValue;

	public float getForceRotationValue() {
		return forceRotationValue;
	}

	public void setForceRotationValue(float forceRotationValue) {
		this.forceRotationValue = forceRotationValue;
	}

	@Transient
	private RefBeamMap refDefMap;

	public RefBeamMap getRefDefMap() {
		return refDefMap;
	}

	public void setRefDefMap(RefBeamMap refDefMap) {
		this.refDefMap = refDefMap;
	}
	
	
}
