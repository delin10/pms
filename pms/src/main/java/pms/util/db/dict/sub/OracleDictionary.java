package pms.util.db.dict.sub;

import pms.util.db.dict.DBOP;
import pms.util.db.dict.Dictionary;

public class OracleDictionary extends Dictionary {
	{
		dict.put(DBOP.PAGE,
				"SELECT * FROM (%s ORDER BY %s %s) WHERE ROWID IN  (SELECT RID FROM (SELECT ROWNUM RN, RID FROM (SELECT ROWID RID, %s FROM (%s ORDER BY %s %s)) WHERE ROWNUM <= %s * %s) WHERE RN > ((%s-1) * %s)) ORDER BY %s %s");
	}

	public String page(String query_sql, String order_col, int start, int size, boolean asc) {
		String order_way = asc ? "asc" : "desc";
		String sql=String.format(dict.get(DBOP.PAGE),query_sql,order_col,order_way,order_col,query_sql,order_col,order_way,start,size,start,size,order_col,order_way);
		return sql;
	}
}
