package pms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestUtil {
	public static String getRequestBody(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer("");
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader((ServletInputStream) request.getInputStream(),"utf-8"))) {
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			return URLDecoder.decode(sb.toString(),"utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static byte[] getBytes(HttpServletRequest request,int length) {
		try {
			InputStream inputStream=request.getInputStream();
			byte[] bytes=FileUtil.getBytesFixedLength(inputStream, length, 5);
			return bytes;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
	}
	
	public static Map<String,String> getParams(String str){
		Map<String,String> map=new HashMap<>();
		String[] kvs=str.split("&");
		
		for (String kv : kvs) {
			String[] kv_=kv.split("=");
			map.put(kv_[0], kv_.length==1?"":kv_[1]);
		}
		
		return map;
	}
	
	public static String getGeneraluri(HttpServletRequest req) {
		String uri=req.getRequestURI();
		StringBuilder str=new StringBuilder(uri);
		return str.toString();
	}
	
	public static void write(HttpServletResponse res, String data, String charset) throws IOException {
		res.setCharacterEncoding(charset);
		res.getWriter().write(data);
	}

	public static void main(String[]args) {
		
	}
}
