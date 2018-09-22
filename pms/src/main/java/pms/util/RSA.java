package pms.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.apache.tomcat.util.codec.binary.Base64;

public class RSA {
	public enum KeyType{PRIVATE_KEY,PUBLIC_KEY};
	private String privateKey="";
	private String publicKey="";
	/**
	 * 加载公钥和私钥
	 * @param url 项目下的文件
	 * @param type 区分私钥和公钥
	 */
	public  void loadKey(String url,KeyType type) {
		String abUrl=FileUtil.removeProtocol(RSA.class.getClassLoader().getResource(url));
		try(BufferedReader reader=Files.newBufferedReader(Paths.get(abUrl))){
			StringBuilder text=new StringBuilder();
			String line=null;
			while((line=reader.readLine())!=null) {
				text.append(line.trim());
			}
			//System.out.println(text);
			if(KeyType.PRIVATE_KEY.equals(type)) {
				privateKey=text.toString();
			}else if (KeyType.PUBLIC_KEY.equals(type)){
				publicKey=text.toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public RSAPublicKey wrapPublicKey() {  
        try {  
        	
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(new Base64().decode(publicKey));  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
  
    public RSAPrivateKey wrapPrivateKey() {  
        try {  
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(new Base64().decode(privateKey));  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    } 
    
    public String sign(String content) {  
    	if (!isReady()) {
    		return null;
    	}
        try {  
            Signature signature = Signature.getInstance("SHA1withRSA");  
            signature.initSign(wrapPrivateKey());  
            signature.update(content.getBytes("utf-8"));  
            return new String(new Base64().encode(signature.sign()));  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    }  
    
    public boolean verify(String content, String sign) {  
    	if (!isReady()) {
    		return false;
    	}
    	
        try {  
            Signature signature = Signature.getInstance("SHA1withRSA");  
            signature.initVerify(wrapPublicKey());  
            signature.update(content.getBytes("utf-8"));  
            return signature.verify((new Base64()).decode(sign));  
        } catch (Exception e) {  
            throw new RuntimeException(e);  
        }  
    } 

	/**
	 * ASCII码转BCD码
	 * 
	 */
	public byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}
	
	public  byte asc_to_bcd(byte asc) {
		byte bcd;
 
		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}
	/**
	 * BCD转字符串
	 */
	public String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;
 
		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
 
			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}
	/**
	 * 拆分字符串
	 */
	public static String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i=0; i<x+z; i++) {
			if (i==x+z-1 && y!=0) {
				str = string.substring(i*len, i*len+y);
			}else{
				str = string.substring(i*len, i*len+len);
			}
			strings[i] = str;
		}
		return strings;
	}
    
   	/**
   	 * 公钥加密
   	 * 
   	 * @param data
   	 * @param publicKey
   	 * @return
   	 * @throws Exception
   	 */
   	public  String encrypt(String data)
   			throws Exception {
   		RSAPublicKey pk=wrapPublicKey();
   		Cipher cipher = Cipher.getInstance("RSA");
   		cipher.init(Cipher.ENCRYPT_MODE, pk);
   		// 模长
   		int key_len = pk.getModulus().bitLength() / 8;
   		// 加密数据长度 <= 模长-11
   		String[] datas = StrUtil.splitString(data, key_len - 11);
   		String mi = "";
   		//如果明文长度大于模长-11则要分组加密
   		for (String s : datas) {
   			mi += bcd2Str(cipher.doFinal(s.getBytes()));
   		}
   		return mi;
   	}
    
   	/**
   	 * 私钥解密
   	 * 
   	 * @param data
   	 * @param privateKey
   	 * @return
   	 * @throws NoSuchPaddingException 
   	 * @throws NoSuchAlgorithmException 
   	 * @throws Exception
   	 */
   	public  String decrypt(String data) throws Exception{
   		RSAPrivateKey pk=wrapPrivateKey();
   		Cipher cipher = Cipher.getInstance("RSA");
   		cipher.init(Cipher.DECRYPT_MODE, pk);
   		//模长
   		int key_len = pk.getModulus().bitLength() / 8;
   		byte[] bytes = data.getBytes();
   		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
   		//如果密文长度大于模长则要分组解密
   		String ming = "";
   		byte[][] arrays = ArrayUtil.splitArray(bcd, key_len);
   		for(byte[] arr : arrays){
   			ming += new String(cipher.doFinal(arr));
   		}
   		return ming;
   	}
    public boolean isReady() {
    	return !publicKey.isEmpty()&&!privateKey.isEmpty();
    }

	public static void main(String[] args) throws Exception {
		RSA rsa=new RSA();
		rsa.loadKey("jieyou/resource/privatekey.pem", KeyType.PRIVATE_KEY);
		rsa.loadKey("jieyou/resource/publickey.pem", KeyType.PUBLIC_KEY);
		String sign=rsa.sign("123");
		System.out.println(sign);
		System.out.println(rsa.verify("123", sign));
		//String str_encrypt=rsa.encrypt("【【】12!@@1");
		System.out.println(rsa.encrypt("1536310650000_123"));
		System.out.println(rsa.decrypt(rsa.bcd2Str(Base64.decodeBase64("SDOcp3d6/pP3rAIJrfNGZ8C5APFCYlpr4y6TESsb83Dx4g2VQeCiHoBdrY2wBnV7B2cjkkokT7ze5SsBLt18pByDWowMnlrI6+X0jXYreeNvQTzqwO06K8lNZtZQQQm0ELCVp+uN+ogth7Utp09XkvZB/IcNSTorWygHg8bb4K3T01tBWNPNyhSPk1EMu+lGw5iwZx/Rnf43/rp0/529IibWojvTQdUTOqygirBstGS4sSoZ5T1Y0vFUbRfjbGIarPW61UZ7dMs1FMwyAJA3wkjteTq5BXj5duZ8rQSUjysA+AajvR1Hea+mxbhnnXjhoBwq6+tj1xjfg/Ewtjhc7Q=="))));
		
	}
}
