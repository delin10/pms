package pms.util.comm.bean;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import pms.util.reflect.Reflector;

public class BeanUtil {
	public static Object mapToBean(Map<String, String> values, Class<?> clazz) {
		try {
			Object object = clazz.getConstructor().newInstance();
			values.entrySet().stream().forEach(entry -> {
				Reflector.set(entry.getKey(), entry.getValue(), object);
			});
			return object;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
}
