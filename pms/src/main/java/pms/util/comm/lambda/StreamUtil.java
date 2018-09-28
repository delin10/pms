package pms.util.comm.lambda;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtil {
	public static <T> HashSet<T> find(Collection<T> source, String key, Function<T, String> func) {
		return (HashSet<T>) source.stream().filter(e -> func.apply(e).contains(key))
				.collect(Collectors.toCollection(HashSet<T>::new));
	}

	public static <T> HashSet<T> find(T[] arr, String key, Function<T, String> func) {
		
		return (HashSet<T>) Arrays.stream(arr).filter(e -> func.apply(e).contains(key))
				.collect(Collectors.toCollection(HashSet<T>::new));
	}
}
