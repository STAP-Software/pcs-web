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

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "FIConfig")
@NamedQueries({
	@NamedQuery(name = "findByMaskTypeAndInstrument", query = "SELECT o from FIConfig o INNER JOIN FETCH o.pupilMaskType p INNER JOIN FETCH o.instrument i "
			+ "where p.pupilMaskTypeId = :pupilMaskTypeId and i.instrumentId = :instrumentId" )
})
public class FIConfig {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long fiConfigId;
	
	private float uEst;
	private float uDelta0;
	
	private int matchbox;
	private int nThresh0;
	private int nCut;
	private int nPeakMinThresh;
	private int nPeakMaxThresh;
	private int lensletOrientation;
	private int spiralRingCount;
	private int thresholdCalcMethod;
	
	@ManyToOne
	@JoinColumn (name="pupilMaskTypeId")
	private PupilMaskType pupilMaskType;

	@ManyToOne
	@JoinColumn (name="instrumentId")
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

	public int getnCut() {
		return nCut;
	}

	public void setnCut(int nCut) {
		this.nCut = nCut;
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

	public int getThresholdCalcMethod() {
		return thresholdCalcMethod;
	}

	public void setThresholdCalcMethod(int thresholdCalcMethod) {
		this.thresholdCalcMethod = thresholdCalcMethod;
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

}
