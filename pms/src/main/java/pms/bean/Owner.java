package pms.bean;

public class Owner{
	private String building_id;
	private String community_name;
	private String room_id;
	private String owner_id;
	
	
	public Owner() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Owner(String building_id, String community_name, String room_id, String owner_id, int floor_id,
			String contract_id, String name, String sex, int age, String tel, String hukou, String work_place,
			String contract_address, String postalcode, String check_in_time, String pay_way, String remark) {
		super();
		this.building_id = building_id;
		this.community_name = community_name;
		this.room_id = room_id;
		this.owner_id = owner_id;
		this.floor_id = floor_id;
		this.contract_id = contract_id;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.tel = tel;
		this.hukou = hukou;
		this.work_place = work_place;
		this.contract_address = contract_address;
		this.postalcode = postalcode;
		this.check_in_time = check_in_time;
		this.pay_way = pay_way;
		this.remark = remark;
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
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	public int getFloor_id() {
		return floor_id;
	}
	public void setFloor_id(String floor_id) {
		this.floor_id = Integer.parseInt(floor_id);
	}
	public String getContract_id() {
		return contract_id;
	}
	public void setContract_id(String contract_id) {
		this.contract_id = contract_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = Integer.parseInt(age);
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getHukou() {
		return hukou;
	}
	public void setHukou(String hukou) {
		this.hukou = hukou;
	}
	public String getWork_place() {
		return work_place;
	}
	public void setWork_place(String work_place) {
		this.work_place = work_place;
	}
	public String getContract_address() {
		return contract_address;
	}
	public void setContract_address(String contract_address) {
		this.contract_address = contract_address;
	}
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getCheck_in_time() {
		return check_in_time;
	}
	public void setCheck_in_time(String check_in_time) {
		this.check_in_time = check_in_time;
	}
	public String getPay_way() {
		return pay_way;
	}
	public void setPay_way(String pay_way) {
		this.pay_way = pay_way;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	private int floor_id;
	private String contract_id;
	private String name;
	private String sex;
	private int age;
	private String tel;
	private String hukou;
	private String work_place;
	private String contract_address;
	private String postalcode;
	private String check_in_time;
	private String pay_way;
	private String remark;
}