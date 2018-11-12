package pms.bean;

public class Building{
	private String building_id;
	private String community_name;
	private String building_type;
	private double floor_area;
	private int floor_num;
	private String direction;
	private double height;
	private String crttime;
	
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
	public String getBuilding_type() {
		return building_type;
	}
	public void setBuilding_type(String building_type) {
		this.building_type = building_type;
	}
	public double getFloor_area() {
		return floor_area;
	}
	public void setFloor_area(String floor_area) {
		this.floor_area = Double.parseDouble(floor_area);
	}
	public int getFloor_num() {
		return floor_num;
	}
	public void setFloor_num(String floor_num) {
		this.floor_num = Integer.parseInt(floor_num);
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = Double.parseDouble(height);
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
	private String description;
}