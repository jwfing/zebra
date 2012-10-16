package org.zebra.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
	public static long FNVHash(String data) {
		final int p = 16777619;
		long hash = 2166136261L;
		for (int i = 0; i < data.length(); i++)
			hash = (hash ^ data.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		return hash;
	}

	public static String computeMD5(String input) {
        return computeMD5(input.getBytes());
    }

    public static String computeMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input, 0, input.length);
            byte[] md5bytes = md.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < md5bytes.length; i++) {
                String hex = Integer.toHexString(0xff & md5bytes[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }
}
