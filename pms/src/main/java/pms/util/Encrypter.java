package pms.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypter {
	public static String Md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		// 确定计算方法
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		// 加密后的字符串
		//System.out.println(str);
		String newstr = Encrypter.toHexString(md5.digest(str.getBytes("utf-8")));
		//System.out.println(newstr);
		return newstr;
	}
	
	public static String toHexString(byte[] src) {
		 StringBuilder stringBuilder = new StringBuilder("");  
		 if (src == null || src.length <= 0) {  
		 return null;  
		 }  
		for (int i = 0; i < src.length; i++) {  
		int v = src[i] & 0xFF;  
		String hv = Integer.toHexString(v);  
		if (hv.length() < 2) {  
		stringBuilder.append(0);  
		}  
		stringBuilder.append(hv);  
		}  
		return stringBuilder.toString();  
	}
	
	public static void main(String[]args) {
		
		System.out.println(Encrypter.toHexString("YiITiz6yNiulQF/NKCZJPkoq7FtocuatuHUwm4S12UPvTo7/Zk5rrGSifOtgUeesXZGJhN0JSgAODpUAbSBd2pNa5dWTeKDhBuIj07g7r+O/324vFXNQSwN/rTs5gixNMZ6fpdNsFLG50ql02YrQq6fFu4UA/uIYOYYfVkUr/mOT/NuppWXU7KEVSjD8vEPNkzY1ZGdrb2yrnxmGliHxM48E0fQnK/gHVn2Nz1SCCKgpDRXqMMcrciRdJZOuMcK4ts+qcHi3cFfPeJ4VjD3UAQlavQDhF4/wj4IVARbGU7bOCaILN/TMzY9O3/eSI3mfjxkCfb+ENk/wmDLSyvbC5Q==".getBytes()));
	}
}
