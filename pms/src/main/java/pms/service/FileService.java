package pms.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import pms.comm.RuntimeStorage;
import pms.util.FormDataProcessor;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;

public class FileService {
	public Info fileUpload() {
		Info info = new Info();
		FormDataProcessor processor = new FormDataProcessor(RuntimeStorage.root + "temp");
		processor.handleFileFields((item, i) -> {
			String fileName = item.getName();
			if (!fileName.trim().isEmpty()) {
				Map<String, InputStream> map = new HashMap<>();
				try {
					map.put("imgurl", item.getInputStream());
					boolean res = DBUtil.updateBigObject("company", new Keys().start(new KV("rownum", "1")), map);
					if (res) {
						info.suc("success");
					} else {
						info.fail("fail");
					}
				} catch (IOException e) {
					info.fail("ÎÄ¼þ½âÎöÊ§°Ü");
				}

			}
		});
		return info;
	}

	public Info fileUpload(byte[] bytes) {
		Info info = new Info();
		Map<String, InputStream> map = new HashMap<>();
		map.put("imgurl", new ByteArrayInputStream(bytes));
		boolean res = DBUtil.updateBigObject("company", new Keys().start(new KV("rownum", "1")), map);
		if (res) {
			info.suc("success");
		} else {
			info.fail("fail");
		}
		return info;
	}
}
