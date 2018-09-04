package pms.util.file;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;

public class FileUtil {
	public static void notExistCreate(String path) {
		File file=new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	public static String onlyDir(String fileUrl) {
		int index=fileUrl.lastIndexOf("/");
		int	indexR=fileUrl.lastIndexOf("\\");
		int end=0;
		if (index>0&&indexR<index) {
			end=index;
		}else if (indexR>0&&indexR>index) {
			end=index;
		}
		return fileUrl.substring(0, end);
	}
	
	public static File newFile(String fp) {
		notExistCreate(onlyDir(fp));
		return new File(fp);
	}
	
	public static File[] getSameNameFiles(String fp) {
		int endIndexE=fp.lastIndexOf(".");
		int endIndexP=fp.lastIndexOf("/");
		String fName=fp.substring(endIndexP+1,
							endIndexE<0?fp.length():endIndexE);
		String fatherPath=fp.substring(0,endIndexP+1);
		File dir=new File(fatherPath);
		File[] files=dir.listFiles((File f,String str)->{
			return str.substring(0, str.lastIndexOf(".")).equals(fName);
		});
		return files;
	}
	
	public static boolean checkFileNameExist(String fp) {
		return getSameNameFiles(fp).length!=0;
	}
	
	public static boolean checkFileNameExistAndDelete(String fp) {
		File[] files=getSameNameFiles(fp);
		if (files.length==0) {
			return false;
		}
		Arrays.stream(files).forEach(File::delete);
		return true;
	}
	
	public static String projectFileAbPath(String relative,Class<?> clazz) throws UnsupportedEncodingException {
		return java.net.URLDecoder.decode(removeProtocol(clazz.getClassLoader().getResource(relative)),"utf-8");
	}
	
	public static void main(String[]args) {
		FileUtil.checkFileNameExistAndDelete("E:/test/.txt");
		SecurityManager s = System.getSecurityManager();
	}
	
	public static String removeProtocol(URL url) {
		System.out.println(url);
		String protocol=url.getProtocol();
		
		return url.toString().replaceAll(protocol+":/", "");
	}
}
