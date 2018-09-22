package pms.util.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import pms.util.auth.bean.Role;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.reflect.anno.Skip;

public class Reflector {
	private static HashMap<Class<?>, HashMap<String, Field>> cache = new HashMap<>();

	public static Object get(Object o, String attr) throws Exception {
		Class<?> clazz = o.getClass();
		HashMap<String, Field> fields = getFields(clazz);
		Field field = fields.get(attr);
		return field.get(o);
	}

	public static HashMap<String, Field> getFields(Class<?> clazz)  {
		return register(clazz);
	}

	public static HashMap<String, Field> register(Class<?> clazz) {
		HashMap<String, Field> v = cache.get(clazz);
		if (v == null) {
			HashMap<String, Field> fields = new HashMap<>();
			Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
				Skip skip = field.getAnnotation(Skip.class);
				if (skip==null||!skip.skip()) {
					String name = field.getName();
					field.setAccessible(true);
					fields.put(name, field);
				}
			});
			cache.put(clazz, fields);
			v = fields;
		}

		return v;
	}
	
	public static void set(String attr,Object value,Object o) {
		HashMap<String, Field> fields = getFields(o.getClass());
		SimpleExec.exec(()->{
			fields.get(attr).set(attr, value);
			return null;
		}, Handler.PRINTTRACE);
	}

	public static void main(String... args) throws Exception {
		int id = 0;
		Role role = new Role();
		role.setId("" + id++);
		role.setAvailable("1");
		role.setDescription("≤‚ ‘");
		role.setName("≤‚ ‘");

		System.out.println(Reflector.get(role, "id"));
	}
}
