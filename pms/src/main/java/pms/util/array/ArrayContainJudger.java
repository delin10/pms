package pms.util.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ArrayContainJudger{
	@SuppressWarnings("rawtypes")
	private static  HashSet set=null;
	public <T>  ArrayContainJudger parse (T[] arr) {
		set=Arrays.stream(arr).collect(Collectors.toCollection(HashSet<T>::new));
		return this;
	}
	
	public <T>  boolean contains(T e) {
		return set.contains(e);
	}
}
