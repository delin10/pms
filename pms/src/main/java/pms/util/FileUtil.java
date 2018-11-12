package pms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import pms.util.array.CollectionUtil;

public class FileUtil {

	public static String readText(String relative, boolean trim) throws IOException {
		StringBuilder text = new StringBuilder();
		try (BufferedReader reader = Files.newBufferedReader(
				Paths.get(FileUtil.removeProtocol(FileUtil.class.getClassLoader().getResource(relative))))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				text.append(trim ? line : line.trim());
			}
		}
		return text.toString();
	}

	public static void notExistCreate(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static boolean isExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static String onlyDir(String fileUrl) {
		int index = fileUrl.lastIndexOf("/");
		int indexR = fileUrl.lastIndexOf("\\");
		int end = 0;
		if (index > 0 && indexR < index) {
			end = index;
		} else if (indexR > 0 && indexR > index) {
			end = index;
		}
		return fileUrl.substring(0, end);
	}

	public static File newFile(String fp) {
		notExistCreate(onlyDir(fp));
		return new File(fp);
	}

	public static File[] getSameNameFiles(String fp) {
		int endIndexE = fp.lastIndexOf(".");
		int endIndexP = fp.lastIndexOf("/");
		String fName = fp.substring(endIndexP + 1, endIndexE < 0 ? fp.length() : endIndexE);
		String fatherPath = fp.substring(0, endIndexP + 1);
		File dir = new File(fatherPath);
		File[] files = dir.listFiles((File f, String str) -> {
			return str.substring(0, str.lastIndexOf(".")).equals(fName);
		});
		return files;
	}

	public static boolean checkFileNameExist(String fp) {
		return getSameNameFiles(fp).length != 0;
	}

	public static boolean checkFileNameExistAndDelete(String fp) {
		File[] files = getSameNameFiles(fp);
		if (files.length == 0) {
			return false;
		}
		Arrays.stream(files).forEach(File::delete);
		return true;
	}

	public static ArrayList<String> getAllFileNameIn(String dir) {
		ArrayList<String> list = new ArrayList<>();
		File file = new File(dir);
		if (!file.exists()) {
			return null;
		}

		if (file.isFile()) {
			list.add(file.getName());
			return list;
		}

		file.listFiles((File f) -> {
			String n = f.getName();
			int end = n.lastIndexOf(".");
			// System.out.println(f.getName());
			if (f.isFile())
				list.add(n.substring(0, end < 0 ? n.length() : end));
			return true;
		});
		return list;
	}

	public static String generateFile(String base, FileNameGenerator generator) {
		FileUtil.notExistCreate(base);
		ArrayList<String> names = getAllFileNameIn(base);
		String name = generator.generate();
		while (names.contains(name))
			name = generator.generate();

		return name;
	}

	public static void main(String[] args) {
		FileUtil.getAllFileNameIn("D:/java2//lib/").stream().forEach(System.out::println);
	}

	public static String removeProtocol(URL url) {
		String protocol = url.getProtocol();
		System.out.println(url.toString().replaceAll(protocol + ":/", ""));

		return url.toString().replaceAll(protocol + ":/", "");
	}

	public static String getwebRoot(String proname) {
		String root = System.getProperty("user.dir");
		String end_str = root.substring(root.lastIndexOf(File.separator) + 1);
		if (end_str.equals("bin")) {
			root = root.substring(0, root.lastIndexOf(File.separator) + 1);
		}

		return root + "webapps/" + proname + "/";
	}

	public static byte[] getBytes(InputStream stream) {
		try {
			// httpclient的流不能关闭
			byte[] buffer = new byte[1024];
			ArrayList<Object> list = new ArrayList<>();
			int len;
			while ((len = stream.read(buffer)) > 0) {
				if (len < 1024) {
					list.add(CollectionUtil.trimArray(buffer, len));
					break;
				}
				list.add(buffer);
				buffer = new byte[1024];
			}
			return (byte[]) CollectionUtil.mergeArrays(list, byte.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] getBytesFixedLength(InputStream stream, int length, int waitTime) {
		try {
			// httpclient的流不能关闭
			byte[] buffer = new byte[1024];
			ArrayList<Object> list = new ArrayList<>();
			int len;
			int size = 0;
			int count = 0;
			while (size < length) {
				while ((len = stream.read(buffer)) < 0 && count < waitTime) {
					System.out.println("正在等待....");
					Thread.sleep(1000);
					++count;
				}
				if (count == waitTime) {
					return null;
				}
				size += len;
				if (len < 1024) {
					list.add(CollectionUtil.trimArray(buffer, len));
				} else {
					list.add(buffer);
				}
				buffer = new byte[1024];
			}
			return (byte[]) CollectionUtil.mergeArrays(list, byte.class);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static void clearDir(String dir) {
		File file = new File(dir);

		if (!file.isDirectory()) {
			return;
		}

		if (!file.exists()) {
			file.mkdirs();
			return;
		}

		Arrays.stream(file.listFiles()).forEach(File::delete);
	}

	public static interface FileNameGenerator {
		public String generate();
	}
}
