package pms.bean;

import java.io.InputStream;

import pms.util.reflect.anno.Skip;

public class Company{
	private String info;
	private String description;
	private String legal_person;
	@Skip(skip=true)
	private InputStream imgUrl;
	private String address;
	private String contact_tel;
	private String contact_email;
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLegal_person() {
		return legal_person;
	}
	public void setLegal_person(String legal_person) {
		this.legal_person = legal_person;
	}
	public InputStream getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(InputStream imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContact_tel() {
		return contact_tel;
	}
	public void setContact_tel(String contact_tel) {
		this.contact_tel = contact_tel;
	}
	public String getContact_email() {
		return contact_email;
	}
	public void setContact_email(String contact_email) {
		this.contact_email = contact_email;
	}
}