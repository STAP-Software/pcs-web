package org.tmt.aps.peas.camera.model;

public class Camera {

	
	public static final int CCD_POWER_STATE_ON = 1;
	public static final int CCD_POWER_STATE_OFF = 2;
	
	private int pupilMask;
	private int filter;
	private int refBeam;
	private Shutter shutter;
	private PreflashLEDs preflashLEDs;
	private CoarseTilt coarseTilt;
	private FineTilt fineTilt;
	private TwoPosMechanism twoPosMechanism;
	private Ccd ccd;
	private float instrumentTemperature;
	private float electronicsBoxTemperature;
	private KnifeEdge knifeEdge;
	private VideoCcd videoCcd;

	public Camera() {
		
	}
	
	public Camera (int pupilMask, int filter, int refBeam, int shutterState, float shutterExposureTime, 
			int preflashLEDState, float preflashLEDFlashDuration, 
			float coarseTiltX, float coarseTiltY, float fineTiltX, float fineTiltY, int twoPosMechanismState, 
			int ccdPower, float ccdTemperature, float instrumentTemperature, float electronicsBoxTemperature, 
			int knifeEdgePositionCommand, float knifeEdgePosition, int knifeEdgeRateCommand, float knifeEdgeRate, 
			float videoCcdExpose, int videoCcdPower) {
		
		this.pupilMask = pupilMask;
		this.filter = filter;
		this.refBeam = refBeam;
		this.shutter = new Shutter(shutterState, shutterExposureTime);
		this.preflashLEDs = new PreflashLEDs(preflashLEDState, preflashLEDFlashDuration);
		this.coarseTilt = new CoarseTilt(coarseTiltX, coarseTiltY);
		this.fineTilt = new FineTilt(fineTiltX, fineTiltY);
		this.twoPosMechanism = new TwoPosMechanism(twoPosMechanismState);
		this.ccd = new Ccd(ccdPower, ccdTemperature);
		this.instrumentTemperature = instrumentTemperature;
		this.electronicsBoxTemperature = electronicsBoxTemperature;
		this.knifeEdge = new KnifeEdge(knifeEdgePositionCommand, knifeEdgePosition, knifeEdgeRateCommand, knifeEdgeRate);
		this.videoCcd = new VideoCcd(videoCcdPower, videoCcdExpose);
	}
	

	public int getPupilMask() {
		return pupilMask;
	}

	public void setPupilMask(int pupilMask) {
		this.pupilMask = pupilMask;
	}

	public int getFilter() {
		return filter;
	}

	public void setFilter(int filter) {
		this.filter = filter;
	}

	public int getRefBeam() {
		return refBeam;
	}

	public void setRefBeam(int refBeam) {
		this.refBeam = refBeam;
	}

	public Shutter getShutter() {
		return shutter;
	}

	public void setShutter(Shutter shutter) {
		this.shutter = shutter;
	}

	public PreflashLEDs getPreflashLEDs() {
		return preflashLEDs;
	}

	public void setPreflashLEDs(PreflashLEDs preflashLEDs) {
		this.preflashLEDs = preflashLEDs;
	}

	public CoarseTilt getCoarseTilt() {
		return coarseTilt;
	}

	public void setCoarseTilt(CoarseTilt coarseTilt) {
		this.coarseTilt = coarseTilt;
	}

	public FineTilt getFineTilt() {
		return fineTilt;
	}

	public void setFineTilt(FineTilt fineTilt) {
		this.fineTilt = fineTilt;
	}

	public TwoPosMechanism getTwoPosMechanism() {
		return twoPosMechanism;
	}

	public void setTwoPosMechanism(TwoPosMechanism twoPosMechanism) {
		this.twoPosMechanism = twoPosMechanism;
	}
	
	public Ccd getCcd() {
		return ccd;
	}

	public void setCcd(Ccd ccd) {
		this.ccd = ccd;
	}

	public float getInstrumentTemperature() {
		return instrumentTemperature;
	}

	public void setInstrumentTemperature(float instrumentTemperature) {
		this.instrumentTemperature = instrumentTemperature;
	}

	public float getElectronicsBoxTemperature() {
		return electronicsBoxTemperature;
	}

	public void setElectronicsBoxTemperature(float electronicsBoxTemperature) {
		this.electronicsBoxTemperature = electronicsBoxTemperature;
	}

	public KnifeEdge getKnifeEdge() {
		return knifeEdge;
	}

	public void setKnifeEdge(KnifeEdge knifeEdge) {
		this.knifeEdge = knifeEdge;
	}

	public VideoCcd getVideoCcd() {
		return videoCcd;
	}

	public void setVideoCcd(VideoCcd videoCcd) {
		this.videoCcd = videoCcd;
	}

}
