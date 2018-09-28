package pms.bean;

public class Charge_form{
	private String form_id;
	private String building_id;
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
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
	public void setFloor_id(int floor_id) {
		this.floor_id = floor_id;
	}
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	public String getPay_way() {
		return pay_way;
	}
	public void setPay_way(String pay_way) {
		this.pay_way = pay_way;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	public int getItem_num() {
		return item_num;
	}
	public void setItem_num(int item_num) {
		this.item_num = item_num;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public double getReceivable() {
		return receivable;
	}
	public void setReceivable(double receivable) {
		this.receivable = receivable;
	}
	public double getReal_receipt() {
		return real_receipt;
	}
	public void setReal_receipt(double real_receipt) {
		this.real_receipt = real_receipt;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public String getForm_creater() {
		return form_creater;
	}
	public void setForm_creater(String form_creater) {
		this.form_creater = form_creater;
	}
	public String getForm_crttime() {
		return form_crttime;
	}
	public void setForm_crttime(String form_crttime) {
		this.form_crttime = form_crttime;
	}
	private String community_name;
	private String room_id;
	private int floor_id;
	private String owner_id;
	private String pay_way;
	private String item_id;
	private int item_num;
	private String start_time;
	private String end_time;
	private double receivable;
	private double real_receipt;
	private double balance;
	private String form_creater;
	private String form_crttime;
}