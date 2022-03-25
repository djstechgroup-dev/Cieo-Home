package com.kinetise.helpers.encoding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Kuba Komorowski on 2014-12-10.
 */
public class MD5encoder {

    public static String encode(String stringToEncode) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(stringToEncode.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(String.format("%02x", messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
