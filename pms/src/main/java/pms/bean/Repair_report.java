package pms.bean;

public class Repair_report{
	private String report_id;
	private String owner_id;
	private String receiver;
	private String contract_tel;
	public String getReport_id() {
		return report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}
	public String getOwner_id() {
		return owner_id;
	}
	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getContract_tel() {
		return contract_tel;
	}
	public void setContract_tel(String contract_tel) {
		this.contract_tel = contract_tel;
	}
	public String getReport_time() {
		return report_time;
	}
	public void setReport_time(String report_time) {
		this.report_time = report_time;
	}
	public String getDistribute_time() {
		return distribute_time;
	}
	public void setDistribute_time(String distribute_time) {
		this.distribute_time = distribute_time;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getWorker() {
		return worker;
	}
	public void setWorker(String worker) {
		this.worker = worker;
	}
	public String getWorket_tel() {
		return worket_tel;
	}
	public void setWorket_tel(String worket_tel) {
		this.worket_tel = worket_tel;
	}
	public String getFinished_time() {
		return finished_time;
	}
	public void setFinished_time(String finished_time) {
		this.finished_time = finished_time;
	}
	private String report_time;
	private String distribute_time;
	private String descript;
	private String worker;
	private String worket_tel;
	private String finished_time;
}