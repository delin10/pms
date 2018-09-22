package pms.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyLoader {
	public static Map<String,String> load(String url){
		Map<String,String> map=new HashMap<>();
		Properties pros = new Properties();
		try (InputStream in = Files.newInputStream(Paths.get(url))) {
			pros.load(in);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		pros.forEach((k,v)->map.put(k.toString(), v.toString()));
		return map;
	}
	
	public static void main(String[] args) {
		System.out.println(PropertyLoader.load(FileUtil.removeProtocol(PropertyLoader.class.getResource("db.properties"))));
	}
}
