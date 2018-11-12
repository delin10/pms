package pms.util.comm.lambda;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StreamUtil {
	public static <T> HashSet<T> find(Collection<T> source, String key, Function<T, String> func) {
		return (HashSet<T>) source.stream().filter(e -> {
			return func.apply(e).contains(key);		
		}).collect(Collectors.toCollection(HashSet<T>::new));
	}

	public static <T> HashSet<T> find(T[] arr, String key, Function<T, String> func) {
		
		return (HashSet<T>) Arrays.stream(arr).filter(e -> func.apply(e).contains(key))
				.collect(Collectors.toCollection(HashSet<T>::new));
	}
	
	@SuppressWarnings({ "rawtypes"})
	public static boolean next(Stream stream) {
		try {
			stream.skip(1);
		}catch(IllegalArgumentException e) {
			return false;
		}
		return true;
	}
	
	public static <S,R,C extends Collection<S>> C parse(S[] arr,Supplier<C> supplier) {
		return Arrays.stream(arr).collect(Collectors.toCollection(supplier));
	}
	
	public static void main(String[] args) {
		parse(new String[] {"a","b"}, HashSet<String>::new).stream().forEach(System.out::println);
	}
}
