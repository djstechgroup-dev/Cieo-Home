package com.kinetise.helpers.encoding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Kuba Komorowski on 2014-12-10.
 */
public class SHAencoder {

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String encode(String stringToEncode)  {
        MessageDigest md;
        byte[] sha1hash;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(stringToEncode.getBytes(), 0, stringToEncode.length());
            sha1hash = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            sha1hash = null;
        }
        return convertToHex(sha1hash);
    }
}
