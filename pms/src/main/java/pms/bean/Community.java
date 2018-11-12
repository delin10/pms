package pms.bean;

public class Community{
	private String name;
	private String address;
	private double floor_area;
	private double total_area;
	private double green_area;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getFloor_area() {
		return floor_area;
	}
	public void setFloor_area(String floor_area) {
		this.floor_area = Double.parseDouble(floor_area);
	}
	public double getTotal_area() {
		return total_area;
	}
	public void setTotal_area(String total_area) {
		this.total_area = Double.parseDouble(total_area);
	}
	public double getGreen_area() {
		return green_area;
	}
	public void setGreen_area(String green_area) {
		this.green_area = Double.parseDouble(green_area);
	}
	public String getCrttime() {
		return crttime;
	}
	public void setCrttime(String crttime) {
		this.crttime = crttime;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	private String crttime;
	private String description;
}