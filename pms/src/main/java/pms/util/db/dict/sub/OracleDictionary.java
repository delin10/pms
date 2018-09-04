package pms.util.db.dict.sub;

import pms.util.db.dict.DBOP;
import pms.util.db.dict.Dictionary;

public class OracleDictionary extends Dictionary {
	{
		dict.put(DBOP.PAGE, "SELECT * FROM %s WHERE ROWID IN  (SELECT RID FROM (SELECT ROWNUM RN, RID FROM (SELECT ROWID RID, %s FROM (select * from %s order by %s %s) ORDER BY %s %s) "
				+ "WHERE ROWNUM <= %s * %s)WHERE RN > ((%s-1) * %s)) ORDER BY %s %s");
	}
	
	public String page(String table,String order_col,int start_page,int size,boolean asc) {
		String order_sql=asc?"asc":"DESC";
		String sql=String.format(dict.get(DBOP.PAGE),table,order_col,table,order_col,order_sql,order_col,order_sql,start_page,size,start_page,size,order_col,order_sql);
		System.out.println(sql);
		return sql;
	}
}

