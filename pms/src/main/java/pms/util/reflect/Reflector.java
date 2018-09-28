package pms.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import pms.util.StrUtil;
import pms.util.auth.bean.Role;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.reflect.anno.Setter;
import pms.util.reflect.anno.Skip;
import pms.util.url.URLParser;
import pms.util.url.URLParser.DURL;

public class Reflector {
	private static HashMap<Class<?>, HashMap<String, Field>> cache = new HashMap<>();
	private static HashMap<Class<?>, HashMap<String, Method>> getters = new HashMap<>();
	private static HashMap<Class<?>, HashMap<String, Method>> setters = new HashMap<>();

	public static Object get(Object o, String attr) throws Exception {
		Class<?> clazz = o.getClass();
		HashMap<String, Field> fields = getFields(clazz);
		Field field = fields.get(attr);
		return field.get(o);
	}

	public static HashMap<String, Field> getFields(Class<?> clazz) {
		return register(clazz);
	}

	public static HashMap<String, Field> register(Class<?> clazz) {
		HashMap<String, Field> v = cache.get(clazz);
		if (v == null) {
			HashMap<String, Field> fields = new HashMap<>();
			Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
				Skip skip = field.getAnnotation(Skip.class);
				if (skip == null || !skip.skip()) {
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

	public static HashMap<String, Method> getters(Class<?> clazz) {
		HashMap<String, Method> methods = getters.containsKey(clazz) ? getters.get(clazz) : new HashMap<>();
		if (methods.size() == 0) {
			HashMap<String, Field> fields = getFields(clazz);
			fields.keySet().forEach(v -> {
				SimpleExec.exec((data) -> {
					String method_name = "get" + StrUtil.firstLetterToUpperCase(v);
					Method method = clazz.getDeclaredMethod(method_name);
					method.setAccessible(true);
					methods.put(v, method);
					return null;
				}, Handler.PRINTTRACE);
			});
		}
		getters.put(clazz, methods);

		return methods;
	}

	public static HashMap<String, Method> setters(Class<?> clazz) {
		HashMap<String, Method> methods = setters.containsKey(clazz) ? setters.get(clazz) : new HashMap<>();
		if (methods.size() == 0) {
			HashMap<String, Field> fields = getFields(clazz);
			fields.entrySet().forEach(e -> {
				SimpleExec.exec((data) -> {
					String method_name = "set" + StrUtil.firstLetterToUpperCase(e.getKey());
					Setter setter = e.getValue().getAnnotation(Setter.class);
					Class<?>[] clazzs = null;
					if (setter == null) {
						clazzs=new Class<?>[]{String.class};
					} else {
						clazzs = setter.setter();
					}
					Method method = clazz.getDeclaredMethod(method_name,clazzs);
					method.setAccessible(true);
					methods.put(e.getKey(), method);
					return null;
				}, Handler.PRINTTRACE);
			});
		}
		setters.put(clazz, methods);

		return methods;
	}

	public static Method setter(Class<?> clazz, String fieldname) {
		return setters(clazz).get(fieldname);
	}

	public static Method getter(Class<?> clazz, String fieldname) {
		return getters(clazz).get(fieldname);
	}

	public static void set(String attr, Object value, Object o) {
		HashMap<String, Field> fields = getFields(o.getClass());
		SimpleExec.exec((data) -> {
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
		HashSet<String> set=new HashSet<String>();
		set.add("p");
		set.add("pos");
		set.add("action");
		set.add("yo_u");
		DURL url=URLParser.parse("http://localhost:12001/pms/setting?action=123&p=1231&you=&pos=");
		System.out.println(url.equals("http://localhost:12001/pms/setting", set));
	}
}
