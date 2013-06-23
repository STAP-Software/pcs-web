package org.tmt.aps.peas;

public class Setup {

	
float coarseMirrorX;
float coarseMirrorY;
int fandIAttempts;		
float cameraRot;  		

boolean removeBadPixels;	
boolean subtractDarkCurrent; 		
boolean flattenField;		

int autoPointTelescope;
String compPhasingPlogFilename;



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

	
	
}
