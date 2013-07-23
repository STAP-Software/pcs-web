package org.tmt.aps.peas.config.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.tmt.aps.peas.instrument.model.Instrument;
import org.tmt.aps.peas.telescope.model.Telescope;

@Entity
@Table(name = "GlobalConfig")
@NamedQueries({
		@NamedQuery(name = "findDefaultConfig", query = "SELECT g from GlobalConfig g INNER JOIN FETCH g.telescope tel INNER JOIN FETCH g.instrument inst "
				+ "WHERE tel.telescopeId = :telescopeId AND inst.instrumentId = :instrumentId AND g.defaultFlg = TRUE "
				+ "ORDER BY g.updateDate desc ")

})
public class GlobalConfig {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	Long globalConfigId;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date updateDate;
	
	boolean defaultFlg;
	
	float coarseMirrorX;
	float coarseMirrorY;
	int fandIAttempts;
	float cameraRot;

	boolean removeBadPixels;
	boolean subtractDarkCurrent;
	boolean flattenField;

	int autoPointTelescope;
	String compPhasingPlogFilename;
	
	private boolean autoDisplayCentroids;
	private boolean autoDisplayCentroidOffsets;
	private boolean autoDisplayAvgCentroidOffsets;
	private boolean autoDisplayActuatorDeltas;
	private boolean autoDisplayProcedureDataLog;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "telescopeId")
	Telescope telescope;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instrumentId")
	Instrument instrument;



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

	public int getFandIAttempts() {
		return fandIAttempts;
	}

	public void setFandIAttempts(int fandIAttempts) {
		this.fandIAttempts = fandIAttempts;
	}

	public float getCameraRot() {
		return cameraRot;
	}

	public void setCameraRot(float cameraRot) {
		this.cameraRot = cameraRot;
	}

	public boolean isRemoveBadPixels() {
		return removeBadPixels;
	}

	public void setRemoveBadPixels(boolean removeBadPixels) {
		this.removeBadPixels = removeBadPixels;
	}

	public boolean isSubtractDarkCurrent() {
		return subtractDarkCurrent;
	}

	public void setSubtractDarkCurrent(boolean subtractDarkCurrent) {
		this.subtractDarkCurrent = subtractDarkCurrent;
	}

	public boolean isFlattenField() {
		return flattenField;
	}

	public void setFlattenField(boolean flattenField) {
		this.flattenField = flattenField;
	}

	public int getAutoPointTelescope() {
		return autoPointTelescope;
	}

	public void setAutoPointTelescope(int autoPointTelescope) {
		this.autoPointTelescope = autoPointTelescope;
	}

	public String getCompPhasingPlogFilename() {
		return compPhasingPlogFilename;
	}

	public void setCompPhasingPlogFilename(String compPhasingPlogFilename) {
		this.compPhasingPlogFilename = compPhasingPlogFilename;
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

	public boolean isDefaultFlg() {
		return defaultFlg;
	}

	public void setDefaultFlg(boolean defaultFlg) {
		this.defaultFlg = defaultFlg;
	}

	public Telescope getTelescope() {
		return telescope;
	}

	public void setTelescope(Telescope telescope) {
		this.telescope = telescope;
	}

	public Instrument getInstrument() {
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

}
