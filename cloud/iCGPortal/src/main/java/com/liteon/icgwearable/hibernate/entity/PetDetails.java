package com.liteon.icgwearable.hibernate.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pet_details")
public class PetDetails implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7951308721596497270L;
	@Id
	@GeneratedValue
	@Column(name = "pet_details_id")
	private int petDetailsid;
	@Column(name = "uuid")
	private String uuid;
	@Column(name = "pet_type")
	private String pettype;
	@Column(name = "pet_name")
	private String petname;
	@Column(name = "size_level")
	private String sizelevel;
	@Column(name = "shape_level")
	private String shapelevel;
	@Column(name = "ornament_level")
	private String ornamentlevel;
	@Column(name = "gladness_level")
	private String gladnesslevel;
	@Column(name = "vigor_level")
	private String vigorlevel;
	@Column(name = "created_date")
	private Date created_date;

	public int getPetDetailsid() {
		return petDetailsid;
	}

	public void setPetDetailsid(int petDetailsid) {
		this.petDetailsid = petDetailsid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public String toString() {
		return "PetDetails [petDetailsid=" + petDetailsid + ", uuid=" + uuid + ", pettype=" + pettype + ", petname="
				+ petname + ", sizelevel=" + sizelevel + ", shapelevel=" + shapelevel + ", ornamentlevel="
				+ ornamentlevel + ", gladnesslevel=" + gladnesslevel + ", vigorlevel=" + vigorlevel + ", created_date="
				+ created_date + "]";
	}

	public String getPettype() {
		return pettype;
	}

	public void setPettype(String pettype) {
		this.pettype = pettype;
	}

	public String getPetname() {
		return petname;
	}

	public void setPetname(String petname) {
		this.petname = petname;
	}

	public String getSizelevel() {
		return sizelevel;
	}

	public void setSizelevel(String sizelevel) {
		this.sizelevel = sizelevel;
	}

	public String getShapelevel() {
		return shapelevel;
	}

	public void setShapelevel(String shapelevel) {
		this.shapelevel = shapelevel;
	}

	public String getOrnamentlevel() {
		return ornamentlevel;
	}

	public void setOrnamentlevel(String ornamentlevel) {
		this.ornamentlevel = ornamentlevel;
	}

	public String getGladnesslevel() {
		return gladnesslevel;
	}

	public void setGladnesslevel(String gladnesslevel) {
		this.gladnesslevel = gladnesslevel;
	}

	public String getVigorlevel() {
		return vigorlevel;
	}

	public void setVigorlevel(String vigorlevel) {
		this.vigorlevel = vigorlevel;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	
}
