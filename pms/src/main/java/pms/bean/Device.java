package pms.bean;

public class Device{
	private String device_id;
	private String name;
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = floor_id;
	}
	public String getBuilding_id() {
		return building_id;
	}
	public void setBuilding_id(String building_id) {
		this.building_id = building_id;
	}
	public String getCommunity_name() {
		return community_name;
	}
	public void setCommunity_name(String community_name) {
		this.community_name = community_name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getInstall_time() {
		return install_time;
	}
	public void setInstall_time(String install_time) {
		this.install_time = install_time;
	}
	public String getBad_time() {
		return bad_time;
	}
	public void setBad_time(String bad_time) {
		this.bad_time = bad_time;
	}
	public String getInstall_man() {
		return install_man;
	}
	public void setInstall_man(String install_man) {
		this.install_man = install_man;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String version;
	private String floor_id;
	private String building_id;
	private String community_name;
	private double price;
	private String install_time;
	private String bad_time;
	private String install_man;
	private String status;
}