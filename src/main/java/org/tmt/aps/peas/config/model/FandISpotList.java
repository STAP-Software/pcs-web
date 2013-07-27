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

import org.tmt.aps.peas.instrument.model.PupilMaskType;

@Entity
@Table(name = "FandISpotList")
@NamedQueries({
	@NamedQuery(name = "findAllFandISpotLists", query = "SELECT o from FandISpotList o" )
})
public class FandISpotList {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long spotListId;
	private Integer ufsSegment;	
	private Integer sufsGroup;	
	private String spotListEncoded;  
	
	@ManyToOne
	@JoinColumn (name="pupilMaskId")
	private PupilMaskType pupilMaskType;

	
	
	public Long getSpotListId() {
		return spotListId;
	}

	public void setSpotListId(Long spotListId) {
		this.spotListId = spotListId;
	}

	public Integer getUfsSegment() {
		return ufsSegment;
	}

	public void setUfsSegment(Integer ufsSegment) {
		this.ufsSegment = ufsSegment;
	}

	public Integer getSufsGroup() {
		return sufsGroup;
	}

	public void setSufsGroup(Integer sufsGroup) {
		this.sufsGroup = sufsGroup;
	}

	public String getSpotListEncoded() {
		return spotListEncoded;
	}

	public void setSpotListEncoded(String spotListEncoded) {
		this.spotListEncoded = spotListEncoded;
	}

	public PupilMaskType getPupilMaskType() {
		return pupilMaskType;
	}

	public void setPupilMaskType(PupilMaskType pupilMaskType) {
		this.pupilMaskType = pupilMaskType;
	}
 

}
