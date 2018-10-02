package pms.util.auth.proxy.impl;

import java.sql.ResultSet;
import java.util.Map;

import pms.util.Encrypter;
import pms.util.auth.bean.User;
import pms.util.auth.proxy.Proxy;
import pms.util.comm.lambda.exception.Handler;
import pms.util.comm.lambda.exception.SimpleExec;
import pms.util.db.DBUtil;
import pms.util.db.DBUtil.KV;
import pms.util.db.DBUtil.Keys;

public class BasicProxy implements Proxy {

	@Override
	public Object verify(String id,String pwd,Map<String,Object> attrs) {
		return SimpleExec.exec(data->{
			//System.out.println(new Keys().start(new KV("id",id)).and(new KV("pwd",Encrypter.Md5(pwd))));
			ResultSet rs=DBUtil.keysQuery("users", new Keys().start(new KV("id",id)).and(new KV("pwd",Encrypter.Md5(pwd))));
			return DBUtil.parse(rs, User.class);
		}, Handler.PRINTTRACE);
	}

	@Override
	public String encrypt_pwd(String pwd, Map<String, Object> attrs) {
		// TODO Auto-generated method stub
		return (String) SimpleExec.exec((data)->{
			return Encrypter.Md5(pwd);
		}, Handler.PRINTTRACE);
	}

}
