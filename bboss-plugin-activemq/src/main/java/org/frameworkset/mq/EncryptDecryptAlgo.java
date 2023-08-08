package org.frameworkset.mq;

import org.frameworkset.util.encoder.Base64Commons;

/**
 * 
 * <p>
 * Title: EncryptDecryptAlgo.java
 * </p>
 * <p>
 * Description:简单的加密和解密算法
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: bboss
 * </p>
 * 
 * @Date May 17, 2009
 * @author xian.liu
 * @version 1.0
 */
public class EncryptDecryptAlgo { // Encrypting a string value.
	/**
	 * 字符串加密
	 * 
	 * @param plainText
	 * @return
	 */
	public String encrypt(String plainText) {
		Base64Commons BASE64Encoder = new Base64Commons();
		String encode = BASE64Encoder.encodeAsString(plainText.getBytes());
		return encode;

	}

	/**
	 * 字节数组加密
	 * 
	 * @param in
	 * @return
	 */
	public byte[] encrypt(byte[] in) {
		Base64Commons BASE64Encoder = new Base64Commons();
		String encode = BASE64Encoder.encodeAsString(in);
		return encode.getBytes();

	}

	/**
	 * 字符串解密
	 * 
	 * @param plainText
	 * @return
	 */
	public String decrypt(String plainText) {
		try {
			Base64Commons BASE64Encoder = new Base64Commons();
			String decode = new String(BASE64Encoder.decode(plainText));
			return decode;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 字节数组解密
	 * 
	 * @param in
	 * @return
	 */
	public byte[] decrypt(byte[] in) {
		try {
			Base64Commons BASE64Encoder = new Base64Commons();
			byte[] decode = BASE64Encoder.decode(new String(in));
			return decode;
		} catch (Exception e) {
			return null;
		}

	}

}
