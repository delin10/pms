package pms.dao;

import java.util.ArrayList;

import pms.util.auth.Getter;
import pms.util.comm.Info;

public class Dao {
	public static Getter get=new Getter();
	public static Info auth(String role_id,ArrayList<String> resource_ids) {
		Info info=new Info();
		ArrayList<Info> failed_infos=new ArrayList<Info>();
		resource_ids.stream().forEach(id->{
			Info info_temp=new Info();
			info_temp=Getter.am.auth(role_id, id);
			info_temp.setInfo("id:"+id+"\r\n"+info_temp.getInfo());
			failed_infos.add(info_temp);
		});
		info.suc("");
		info.setData(failed_infos);
		return info;
	}
}
