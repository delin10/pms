package pms.util;

import java.io.IOException;
import java.io.InputStream;

public class NetUtil {
	public static String handleStream(InputStream input) throws IOException {
		byte[] data = new byte[input.available()];
		input.read(data);
		return new String(data);
	}
}
