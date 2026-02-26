/**
 * @author Scott Michaels
 * Copyright (C) 2013 Thirty Meter Telescope Corporation. 
 * All Rights Reserved.
 */
package org.tmt.aps.peas.config.business;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.tmt.aps.peas.common.FloatPoint;
import org.tmt.aps.peas.config.model.MaskConstants;
import org.tmt.aps.peas.config.model.PhasingConstants;
import org.tmt.aps.peas.config.model.PrimaryMirrorConstants;
import org.tmt.aps.peas.config.model.PrimaryMirrorSegmentConstants;
import org.tmt.aps.peas.config.model.SufsConstants;
import org.tmt.aps.peas.config.model.TelescopeConstants;
import org.tmt.aps.peas.instrument.model.PupilMaskType;
import org.tmt.aps.peas.refBeamMap.business.CentroidMapMgmt;

/**
 * EJB Singleton cache for all constants.  On initialization, this EJB calls {@link org.tmt.aps.peas.config.business.ConstantsMgmt#loadConstants(List)} to pull all constant values from the database into this cache. 
 * Provides methods for reading constants values.  This EJB is initialized on startup.
 * @author smichaels
 */
@Singleton
@Startup
@Named
public class ConstantsCache {

	Logger logger = Logger.getLogger(this.getClass());

	@EJB
	ConstantsMgmt constantsMgmt;
	@EJB
	CentroidMapMgmt centroidMapMgmt;
	
	private PrimaryMirrorConstants primaryMirrorConstants;
	private PrimaryMirrorSegmentConstants primaryMirrorSegmentConstants;
	private PhasingConstants phasingConstants;
	private SufsConstants sufsConstants;
	private TelescopeConstants telescopeConstants;
	private MaskConstants maskConstants;
	

	@PostConstruct
	public void init() {
	    try {
	      
			primaryMirrorConstants = new PrimaryMirrorConstants();
			primaryMirrorSegmentConstants = new PrimaryMirrorSegmentConstants();
			phasingConstants = new PhasingConstants();
			sufsConstants = new SufsConstants();
			telescopeConstants = new TelescopeConstants();
			maskConstants = new MaskConstants();
			
			List<Object> instances = new ArrayList<Object>();
			instances.add(primaryMirrorConstants);
			instances.add(primaryMirrorSegmentConstants);
			instances.add(phasingConstants);
			instances.add(sufsConstants);
			instances.add(telescopeConstants);
			instances.add(maskConstants);
			
			// populate constants
			constantsMgmt.loadConstants(instances);
			
			logger.info("\n\nPrimary Mirror Constants: \n" + primaryMirrorConstants);
			logger.info("\n\nPrimary Mirror Segment Constants: \n" + primaryMirrorSegmentConstants);
			logger.info("\n\nPhasing Constants: \n" + phasingConstants);
			logger.info("\n\nSUFS Constants: \n" + sufsConstants);
			logger.info("\n\nTelescope Constants: \n" + telescopeConstants);
			logger.info("\n\nMask Constants: \n" + maskConstants);
		
	    } catch (Exception e) {
	        throw new IllegalStateException("Initialization failed", e);
	    }
	}

	
	
	
	public FloatPoint[] getMaskTheoreticalLocations(Long maskTypeId, Integer sufsGroupNumber) {
		
		System.out.println(maskTypeId);
		
		if (maskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_36)) {
			
			return maskConstants.getPassiveTiltTheoreticalLocations();
			
		} else if (maskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_160)) {
			
			return maskConstants.getPhasingTheoreticalLocations();
			
		} else if (maskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_508)) {
			
			return maskConstants.getFineScreenTheoreticalLocations();
			
		} else if (maskTypeId.equals(PupilMaskType.PUPIL_MASK_TYPE_ID_SUFS)) {
			
			return translateToSufsGroup(maskConstants.getSufsTheoreticalLocations(), sufsGroupNumber);
			
		} 
		
		return null;
	}
	
	private FloatPoint[] translateToSufsGroup(FloatPoint[] subaperatures, Integer sufsGroupNumber) {
	
		
		if (sufsGroupNumber.intValue() == 0) {
			return subaperatures;
		}
		
		// derive sufs locations for group
		FloatPoint[] translated = new FloatPoint[subaperatures.length];
		FloatPoint[] transformed = new FloatPoint[subaperatures.length];
		
		// translate the locations to be centered over another segment
		int groupCenterMirror = sufsConstants.getSufsGroupToMirror()[sufsGroupNumber.intValue() - 1][0];
		FloatPoint groupTranslation = primaryMirrorConstants.getSegmentCenters()[groupCenterMirror-1]; // position of the segment center of the segment this group is centered over
		
		//System.out.println("groupTranslation = " + groupTranslation);
		
		for (int i=0; i<subaperatures.length; i++) {
			
			transformed[i] = new FloatPoint(-subaperatures[i].x, -subaperatures[i].y);
			translated[i] = transformed[i].add(groupTranslation);
			
		}
		
		return translated;
	}	
	
	

	public PrimaryMirrorConstants getPrimaryMirrorConstants() {
		return primaryMirrorConstants;
	}

	public PrimaryMirrorSegmentConstants getPrimaryMirrorSegmentConstants() {
		return primaryMirrorSegmentConstants;
	}

	public PhasingConstants getPhasingConstants() {
		return phasingConstants;
	}

	public SufsConstants getSufsConstants() {
		return sufsConstants;
	}

	public TelescopeConstants getTelescopeConstants() {
		return telescopeConstants;
	}

	public MaskConstants getMaskConstants() {
		return maskConstants;
	}

	
	
	
	
}
