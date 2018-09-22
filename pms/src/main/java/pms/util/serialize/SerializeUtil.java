package pms.util.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	@SuppressWarnings("finally")
	public static byte[] serialize(Object o) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(byteStream)) {
			out.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return byteStream.toByteArray();
		}
	}

	@SuppressWarnings("finally")
	public static Object resolve(byte[] bytes) {
		if (bytes==null) {
			return null;
		}
		ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
		Object o = null;
		try (ObjectInputStream in = new ObjectInputStream(byteStream)) {
			o = in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return o;
		}
	}
}
