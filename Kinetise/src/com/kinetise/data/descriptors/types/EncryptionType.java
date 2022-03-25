package com.kinetise.data.descriptors.types;

public enum EncryptionType {
    NONE, SHA1, MD5;

    public static EncryptionType parseFromString(String value) {
        switch (value) {
            case "md5":
                return MD5;
            case "sha1":
                return SHA1;
            default:
            case "_NONE_":
                return NONE;
        }
    }
}
