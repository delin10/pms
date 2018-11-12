package pms.bean;

import java.io.InputStream;

import pms.util.reflect.anno.Skip;

public class Server_contract_view {
			private String employee_name;
			private String contract_id;
			private String crttime;
			private String deadtime;

			public String getEmployee_name() {
				return employee_name;
			}
			public void setEmployee_name(String employee_name) {
				this.employee_name = employee_name;
			}
			public String getContract_id() {
				return contract_id;
			}
			public void setContract_id(String contract_id) {
				this.contract_id = contract_id;
			}
			public String getCrttime() {
				return crttime;
			}
			public void setCrttime(String crttime) {
				this.crttime = crttime;
			}
			public String getDeadtime() {
				return deadtime;
			}
			public void setDeadtime(String deadtime) {
				this.deadtime = deadtime;
			}
			public int getValid() {
				return valid;
			}
			public void setValid(String valid) {
				this.valid = Integer.parseInt(valid);
			}
			public InputStream getImg() {
				return img;
			}
			public void setImg(InputStream img) {
				this.img = img;
			}
			public String getCreator() {
				return creator;
			}
			public void setCreator(String creator) {
				this.creator = creator;
			}
			private int valid;
			@Skip(skip=true)
			private InputStream img;
			private String creator;
}
