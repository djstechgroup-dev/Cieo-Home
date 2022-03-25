package com.kinetise.data.descriptors.types;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class PasswordString extends FormString {
    private EncryptionType mEncryptionType;

    public PasswordString(String value) {
        super(value);
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        mEncryptionType = encryptionType;
    }

    public String getEncryptedValue() {
        return encrypt(mOriginalValue, mEncryptionType);
    }

    public PasswordString copy(){
        PasswordString result = new PasswordString(getOriginalValue());
        result.setEncryptionType(mEncryptionType);
        return result;
    }

    private String encrypt(String password, EncryptionType encryption) {
        if(encryption==null)
            return password;
        switch (encryption) {
            case MD5:
            case SHA1:
                try {
                    MessageDigest crypt = MessageDigest.getInstance(encryption.name());
                    crypt.reset();
                    crypt.update(password.getBytes("UTF-8"));
                    return byteToHex(crypt.digest());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return "";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
            case NONE:
                return password;
            default:
                throw new IllegalArgumentException(String.format(
                        "Unknown encryption format '%s'", encryption));
        }
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    @Override
    public String toString() {
        return getEncryptedValue();
    }
}
