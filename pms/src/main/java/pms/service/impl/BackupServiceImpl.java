package pms.service.impl;

import java.util.Arrays;
import java.util.Map;

import pms.comm.RuntimeStorage;
import pms.service.BasicService;
import pms.util.auth.manager.Session;
import pms.util.comm.Info;
import pms.util.db.DBUtil;
import pms.util.file.FileUtil;

public class BackupServiceImpl extends BasicService {
	@Override
	public Info add(Session session, Object values) {
		Info info = new Info();
		DBUtil.backup_exp(RuntimeStorage.getPath(), 1024);
		info.suc("success");
		return info;
	}

	@Override
	public Info query(Session session) {
		Info info = new Info();
		info.setData(FileUtil.recursiveFileIn(RuntimeStorage.getPath()));
		info.suc("success");
		return info;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Info delete(Session session, Object paths) {
		Info info = new Info();
		info.suc("删除结果:");
		Arrays.stream(((Map<String, String>[]) paths)).forEach(pathEntry -> {
			String path = pathEntry.get("path");
			boolean res = FileUtil.deleteFile(path);
			info.line();
			if (res) {
				info.append("成功刪除文件:" + path);
			} else {
				info.append("刪除失敗" + path);
			}
		});
		return info;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Info update(Session session, Object values, Map<String, String> keys) {
		// TODO Auto-generated method stub
		Info info = new Info();
		DBUtil.restore_imp(((Map<String, String>) values).get("path"), 1024);
		info.suc("success");
		return info;
	}

	@Override
	public String getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCommunityColumn() {
		// TODO Auto-generated method stub
		return null;
	}

}
