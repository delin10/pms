package pms.util.db;

import java.io.Closeable;
import java.sql.SQLException;

public interface Session extends Closeable{ 
	public boolean persist() throws SQLException;
	public void close();
}