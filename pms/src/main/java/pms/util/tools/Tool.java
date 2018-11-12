package pms.util.tools;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import pms.util.comm.lambda.DStream;
import pms.util.file.FileUtil;
import pms.util.regex.PatternUtil;

public class Tool {
	public static String[] servletUrlGenerator(String servlet_source) {
		String parameter_pattern = "String\\s*([\\d\\w_]*)\\s*=\\s*[^;]*\\.getParameter\\s*\\(\\s*\"([^\"]*)\"\\)\\s*;";
		String parameter_value_pattern = "if\\s*\\(\\s*\"([^\"]*)\"\\s*\\.\\s*equals\\(\\s*%s\\s*\\)|if\\s*\\(\\s*%s\\s*\\.\\s*equals\\s*\\(\\s*\"([^\"]*?)\"\\s*\\)|if\\s*\\(\\s*\"([^\"]*)\"\\s*\\.\\s*equalsIgnoreCase\\(\\s*%s\\s*\\)|if\\s*\\(\\s*%s\\s*\\.\\s*equalsIgnoreCase\\s*\\(\\s*\"([^\"]*?)\"\\s*\\)";
		String match_block = "if\\s*\\(\\s*\"%s\"\\s*\\.\\s*equals\\s*\\(\\s*%s\\s*\\)[^\\{]*\\{([^\\}]*)[^\\w\\d_\"]%s[^\\w\\d\"_][^}]*\\}|if\\s*\\(\\s*%s\\s*\\.\\s*equals\\s*\\(\\s*\"%s\"\\s*\\)[^\\{]*\\{([^\\}]*)[^\\w\\d_\"]%s[^\\w\\d\"_][^}]*\\}|if\\s*\\(\\s*\"%s\"\\s*\\.\\s*equalsIgnoreCase\\s*\\(\\s*%s\\s*\\)[^\\{]*\\{([^\\}]*)[^\\w\\d_\"]%s[^\\w\\d\"_][^}]*\\}|if\\s*\\(\\s*%s\\s*\\.\\s*equalsIgnoreCase\\s*\\(\\s*\"%s\"\\s*\\)[^\\{]*\\{([^\\}]*)[^\\w\\d_\"]%s[^\\w\\d\"_][^}]*\\}";
		HashMap<String, String> param_maps = new HashMap<>();
		PatternUtil.allMatch(servlet_source, parameter_pattern).stream()
				.forEach(groups -> param_maps.put(groups.get(1), groups.get(2)));
		ArrayList<String> url_list = new ArrayList<>();
		param_maps.keySet().forEach(param -> {
			// System.out.println(param);
			ArrayList<ArrayList<String>> groups_all = PatternUtil.allMatch(servlet_source,
					String.format(parameter_value_pattern, param, param, param, param));
			if (groups_all.size() != 0) {
				groups_all.stream().forEach(groups -> {
					String group = groups.get(1);
					if (group == null) {
						group = groups.get(2);
						if (group == null) {
							group = groups.get(3);
							if (group == null) {
								group = groups.get(4);
							}
						}
					}

					String block_action = group;
					// System.out.println("block:" + block_action);

					if (group != null) {
						StringBuilder url = new StringBuilder("?");
						url.append(param);
						url.append("=");
						url.append(block_action);
						url.append("&");
						param_maps.entrySet().stream().forEach(p -> {
							if (!p.getKey().equals(param)) {
								String res = PatternUtil.match(String.format(match_block, block_action, param,
										p.getKey(), param, block_action, p.getKey(), block_action, param, p.getKey(),
										param, block_action, p.getKey()), servlet_source);
								// System.out.println("p在不在块里");
								// System.out.println(res);
								if (!res.isEmpty()) {
									url.append(p.getValue());
									url.append("=%s&");
								}
							}
						});
						url.delete(url.length() - 1, url.length());
						url_list.add(url.toString());
					}
				});
			}
		});
		return url_list.toArray(new String[0]);
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		// String parameter_value_pattern =
		// "if\\s*\\(\\s*\"(.*)\"\\s*\\.\\s*equals\\(\\s*%s\\s*\\)|if\\s*\\(\\s*%s\\s*\\.\\s*equals\\s*\\(\\s*\"(.*?)\"\\s*\\)";
		String parameter_value_pattern = "if\\s*\\(\\s*\".*\"\\s*\\.\\s*equals\\(\\s*%s\\s*\\)[^\\{]*\\{[^}]*[^\\w\\d_\"]*%s[^\\w\\d_\"]*[^\\}]*\\}|if\\s*\\(\\s*%s\\s*\\.\\s*equals\\s*\\(\\s*\"(.*?)\"\\s*\\)[^\\{]*\\{[^}]*[^\\w\\d_\"]*%s[^\\w\\d_\"]*[^\\}]*\\}";
		// String parameter_pattern =
		// "String\\s*(.*?)\\s*\\=\\s*[^;]*\\.getParameter\\s*\\(\\s*[^;]*\\s*\\)\\s*;";
		//
		System.out.println(parameter_value_pattern);
		String[] servlets = { "BuildingServlet", "CarServlet", "CommunityServlet", "CompanyServlet", "ContractServlet",
				"DepartmentServlet", "EmployeeServlet", "InitServlet", "LoginServlet", "OwnerFamilyServlet",
				"OwnerServlet", "PetServlet", "RoomServlet", "ServerContractServlet", "SystemServlet" };
		String[] servletmaps = { "building", "car", "community", "company", "contract", "department", "employee",
				"init", "login", "ownerFamily", "owner", "pet", "room", "serverContract", "setting" };
		DStream.newInstance().merge(Arrays.stream(servlets),Arrays.stream(servletmaps)).mergeMap((arr) -> {
			String text = FileUtil.readText("\\src\\main\\java\\pms\\servlet/" + arr[0] + ".java");
			String[] urls = servletUrlGenerator(text);
			Arrays.stream(urls).map(url->arr[1]+url).forEach(System.out::println);
			return null;
		});
	}
}
