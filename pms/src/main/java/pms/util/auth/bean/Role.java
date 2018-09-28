package pms.util.auth.bean;

import java.io.Serializable;

import pms.util.excel.anno.ExcelColumn;
import pms.util.reflect.anno.Skip;
//roles
public class Role implements Serializable{
	@Skip(skip=true)
	private static final long serialVersionUID = 3443269212386010490L;
	@ExcelColumn(Column="��ɫid")
	private String id;
	@ExcelColumn(Column="��ɫ����")
	private String name;
	@ExcelColumn(Column="��ɫ����")
	private String description;
	@ExcelColumn(Column="�Ƿ����")
	private String available;
	public Role() {
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getAvailable() {
		return Integer.parseInt(available);
	}
	public void setAvailable(String available) {
		this.available = available;
	}
	
	public void id() {
		
	}
}
