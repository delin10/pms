package pms.util.db.dict;

import java.util.HashMap;

public class Dictionary {
	protected HashMap<DBOP,String> dict=new HashMap<>();
	
	public String query(DBOP op) {
		return dict.get(op);
	}
}
