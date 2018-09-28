package pms.bean;

public class Device_check{
	private String device_id;
	private String check_frequency;
	private String descript;
	private String result;
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getCheck_frequency() {
		return check_frequency;
	}
	public void setCheck_frequency(String check_frequency) {
		this.check_frequency = check_frequency;
	}
	public String getDescript() {
		return descript;
	}
	public void setDescript(String descript) {
		this.descript = descript;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	private String checker;
}