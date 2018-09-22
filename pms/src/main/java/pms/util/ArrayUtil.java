package pms.util;

import pms.util.array.ArrayContainJudger;

public class ArrayUtil {
	private static ArrayContainJudger containJudger=null;
	
	public static <T> ArrayContainJudger parseContainJudger(T[] arr) {
		if (containJudger==null) {
			containJudger=new ArrayContainJudger();
		}
		containJudger.parse(arr);
		return containJudger;
	}
	
	public static <T> boolean contains(T e) {
		return containJudger.contains(e);
	}
	
	public static byte[][] splitArray(byte[] data,int len){
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if(y!=0){
			z = 1;
		}
		byte[][] arrays = new byte[x+z][];
		byte[] arr;
		for(int i=0; i<x+z; i++){
			arr = new byte[len];
			if(i==x+z-1 && y!=0){
				System.arraycopy(data, i*len, arr, 0, y);
			}else{
				System.arraycopy(data, i*len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

}
