package org.tmt.aps.peas.instrument.model;

import org.tmt.aps.peas.extinf.Gain;

public class CcdState {

	int imageWidth;
	int imageHeight;
	int overscannedImageWidth;
	int overscannedImageHeight;
	int gainNumber;
	float gainValue;
	int channelOffset1;
	int channelOffset2;
	double temperatureSetting;
	double caseTemperature;
	double rightTemperature;
	double leftTemperature;
	double exposureTime;
	
	public CcdState() {
		
	}
	
	public CcdState(Gain gain, int[] offsets, int[] imageSize, int[] overscannedImageSize, double temperatureSetting, double[] temperatures, double exposureTime) {
		gainNumber = gain.getGain();
		gainValue = (float)gain.getElectronsPerAdu();
		channelOffset1 = offsets[0];
		channelOffset2 = offsets[1];
		imageWidth = imageSize[0];
		imageHeight = imageSize[1];
		overscannedImageWidth = overscannedImageSize[0];
		overscannedImageHeight = overscannedImageSize[1];
		this.temperatureSetting = temperatureSetting;
		caseTemperature = temperatures[0];
		leftTemperature = temperatures[1];
		rightTemperature = temperatures[2];
		this.exposureTime = exposureTime;
	}
	
	public int getImageWidth() {
		return imageWidth;
	}
	
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	
	public int getImageHeight() {
		return imageHeight;
	}
	
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	
	public int getOverscannedImageWidth() {
		return overscannedImageWidth;
	}

	public void setOverscannedImageWidth(int overscannedImageWidth) {
		this.overscannedImageWidth = overscannedImageWidth;
	}
	
	public int getOverscannedImageHeight() {
		return overscannedImageHeight;
	}

	public void setOverscannedImageHeight(int overscannedImageHeight) {
		this.overscannedImageHeight = overscannedImageHeight;
	}

	public int getGainNumber() {
		return gainNumber;
	}
	
	public void setGainNumber(int gainNumber) {
		this.gainNumber = gainNumber;
	}
	
	public float getGainValue() {
		return gainValue;
	}
	
	public void setGainValue(float gainValue) {
		this.gainValue = gainValue;
	}
	
	public int getChannelOffset1() {
		return channelOffset1;
	}
	
	public void setChannelOffset1(int channelOffset1) {
		this.channelOffset1 = channelOffset1;
	}
	
	public int getChannelOffset2() {
		return channelOffset2;
	}
	
	public void setChannelOffset2(int channelOffset2) {
		this.channelOffset2 = channelOffset2;
	}

	public double getTemperatureSetting() {
		return temperatureSetting;
	}

	public void setTemperatureSetting(double temperatureSetting) {
		this.temperatureSetting = temperatureSetting;
	}

	public double getCaseTemperature() {
		return caseTemperature;
	}

	public void setCaseTemperature(double caseTemperature) {
		this.caseTemperature = caseTemperature;
	}

	public double getRightTemperature() {
		return rightTemperature;
	}

	public void setRightTemperature(double rightTemperature) {
		this.rightTemperature = rightTemperature;
	}

	public double getLeftTemperature() {
		return leftTemperature;
	}

	public void setLeftTemperature(double leftTemperature) {
		this.leftTemperature = leftTemperature;
	}

	public double getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(double exposureTime) {
		this.exposureTime = exposureTime;
	}
	
	
	
}
