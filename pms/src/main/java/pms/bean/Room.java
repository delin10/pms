package pms.bean;

public class Room{
	private String building_id;
	private String community_name;
	private String room_id;
	private int floor_id;
	private String room_layout;
	private double room_area;
	private String room_type;
	private String room_use;
	
	public Room() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Room(String building_id, String community_name, String room_id, int floor_id, String room_layout,
			double room_area, String room_type, String room_use, int is_vacancy, int decorated) {
		super();
		this.building_id = building_id;
		this.community_name = community_name;
		this.room_id = room_id;
		this.floor_id = floor_id;
		this.room_layout = room_layout;
		this.room_area = room_area;
		this.room_type = room_type;
		this.room_use = room_use;
		this.is_vacancy = is_vacancy;
		this.decorated = decorated;
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
	public String getRoom_id() {
		return room_id;
	}
	public void setRoom_id(String room_id) {
		this.room_id = room_id;
	}
	public int getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = Integer.parseInt(floor_id);
	}
	public String getRoom_layout() {
		return room_layout;
	}
	public void setRoom_layout(String room_layout) {
		this.room_layout = room_layout;
	}
	public double getRoom_area() {
		return room_area;
	}
	public void setRoom_area(String room_area) {
		this.room_area = Double.parseDouble(room_area);
	}
	public String getRoom_type() {
		return room_type;
	}
	public void setRoom_type(String room_type) {
		this.room_type = room_type;
	}
	public String getRoom_use() {
		return room_use;
	}
	public void setRoom_use(String room_use) {
		this.room_use = room_use;
	}
	public int getIs_vacancy() {
		return is_vacancy;
	}
	public void setIs_vacancy(String is_vacancy) {
		this.is_vacancy = Integer.parseInt(is_vacancy);
	}
	public int getDecorated() {
		return decorated;
	}
	public void setDecorated(String decorated) {
		this.decorated = Integer.parseInt(decorated);
	}
	private int is_vacancy;
	private int decorated;
}