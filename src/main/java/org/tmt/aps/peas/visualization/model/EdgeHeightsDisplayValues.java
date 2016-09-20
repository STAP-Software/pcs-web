package org.tmt.aps.peas.visualization.model;

/**
 * Interface for data required to display edge heights visual display.
 * Classes that implement this interface can be used to display edge heights.
 * @author smichaels
 */
public interface EdgeHeightsDisplayValues {

	
	public float[] getStepCorr(); // yes

	//public float[] getActCalc();  //  not used yet

	public float[] getResid(); // yes

	//public int[] getRowFlagIn(); 

	public int[] getRowFlagOut(); // yes

	//public int getConstrainedSegmentCount();

	//public float getSegmentPistonRms();

	public int getGoodEdgeCount();

	public float getEdgeErrorMax(); 
	
	public void setEdgeErrorMax(float edgeErrorMax);

	public float getEdgeErrorRss();

	public float getResidualEdgeErrorMax(); 
	
	public void setResidualEdgeErrorMax(float residualEdgeErrorMax);

	public float getResidualEdgeErrorRss();

}
