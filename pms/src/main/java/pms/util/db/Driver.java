package pms.util.db;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import pms.util.FileUtil;
import pms.util.ParamsChecker;

public final class Driver {
	private String driver_name;
	private String driver_version;
	private String db_name;
	private String db_version;
	private String driver;
	private String url;
	private String password;
	private String username;
	
	public void configure(String path) throws Exception {
		Properties pros = new Properties();
		try (InputStream in = Files.newInputStream(Paths.get(FileUtil.removeProtocol(Driver.class.getClassLoader().getResource(path))))) {
			pros.load(in);
		}
		String driver = pros.getProperty("jdbc.driver");
		String url = pros.getProperty("jdbc.url");
		String username=pros.getProperty("jdbc.username");
		String password = pros.getProperty("jdbc.password");
		
		if (ParamsChecker.existsNullOrEmpty(driver,url,username,password)) {
			this.driver=driver;
			this.url=url;
			this.password=password;
			this.username=username;
		}else {
			throw new Exception("params are lack");
		}
		
		Class.forName(driver);
		Connection conn=getConnection();
		DatabaseMetaData meta=conn.getMetaData();
		this.db_name=meta.getDatabaseProductName();
		this.db_version=meta.getDatabaseProductVersion();
		this.driver_name=meta.getDriverName();
		this.driver_version=meta.getDriverVersion();
		conn.close();
	}
	
	public String getDriver_name() {
		return driver_name;
	}

	public void setDriver_name(String driver_name) {
		this.driver_name = driver_name;
	}

	public String getDriver_version() {
		return driver_version;
	}

	public void setDriver_version(String driver_version) {
		this.driver_version = driver_version;
	}

	public String getDb_name() {
		return db_name;
	}

	public void setDb_name(String db_name) {
		this.db_name = db_name;
	}

	public String getDb_version() {
		return db_version;
	}

	public void setDb_version(String db_version) {
		this.db_version = db_version;
	}

	public Connection getConnection() throws SQLException {
		return (Connection) DriverManager.getConnection(url, username, password);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String toString() {
		StringBuilder str=new StringBuilder();
		str.append("driver-name:");
		str.append(this.driver_name);
		str.append("\r\n");
		str.append("driver-version:");
		str.append(this.driver_version);
		str.append("\r\n");
		str.append("database-name:");
		str.append(this.db_name);
		str.append("\r\n");
		str.append("database-version");
		str.append(this.db_version);
		str.append("\r\n");
		str.append("user-name:");
		str.append(this.username);
		return str.toString();
	}
}
