package pms.util.file.bean;

import java.util.concurrent.TimeUnit;

import pms.util.DateTimeUtil;

public class FileBean {
	private String path;
	private String name;
	private String last_modified;
	public String getPath() {
		return path;
	}
	public FileBean setPath(String path) {
		this.path = path;
		return this;
	}
	public String getName() {
		return name;
	}
	public FileBean setName(String name) {
		this.name = name;
		return this;
	}
	public String getLast_modified() {
		return DateTimeUtil.format(Long.parseLong(last_modified),TimeUnit.MILLISECONDS);
	}
	public FileBean setLast_modified(String last_modified) {
		this.last_modified = last_modified;
		return this;
	}
	public String toString() {
		return "name:"+name+"\r\n"+
				"path:"+path+"\r\n"+
				"last_modified:"+ DateTimeUtil.format(Long.parseLong(last_modified),TimeUnit.MILLISECONDS) +"\r\n";
	}
}
