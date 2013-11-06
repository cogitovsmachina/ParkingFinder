package mx.essentialab.model;

import java.io.Serializable;

public class SFParkingLot implements Serializable {

	private static final long serialVersionUID = 1L;

	private String lotType;
	private String lotName;
	private String lotDesc;
	private String lotInter;
	private String lotOcc;
	private String lotOper;
	private String lotPts;
	private String lotLoc;
	
	public String getLotType() {
		return lotType;
	}
	
	public void setLotType(String lotType) {
		this.lotType = lotType;
	}
	
	public String getLotName() {
		return lotName;
	}
	
	public void setLotName(String lotName) {
		this.lotName = lotName;
	}
	
	public String getLotDesc() {
		return lotDesc;
	}
	
	public void setLotDesc(String lotDesc) {
		this.lotDesc = lotDesc;
	}
	
	public String getLotInter() {
		return lotInter;
	}
	
	public void setLotInter(String lotInter) {
		this.lotInter = lotInter;
	}
	
	public String getLotOcc() {
		return lotOcc;
	}
	
	public void setLotOcc(String lotOcc) {
		this.lotOcc = lotOcc;
	}
	
	public String getLotOper() {
		return lotOper;
	}
	
	public void setLotOper(String lotOper) {
		this.lotOper = lotOper;
	}
	
	public String getLotPts() {
		return lotPts;
	}
	
	public void setLotPts(String lotPts) {
		this.lotPts = lotPts;
	}
	
	public String getLotLoc() {
		return lotLoc;
	}
	
	public void setLotLoc(String lotLoc) {
		this.lotLoc = lotLoc;
	}

}
