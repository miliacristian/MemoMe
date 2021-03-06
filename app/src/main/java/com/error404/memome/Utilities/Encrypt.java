package com.error404.memome.Utilities;

import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class  Encrypt {//Classe per cifrare e decifrare stringhe

    private final static String AES="AES";
    private final static String HEX = "0123456789ABCDEF";
    private final static String SHA1PRNG="SHA1PRNG";
    //metodo che data una stringa in chiaro e una chiave ritorna una  stringa cifrata con la rispettiva chiave
    public static String encryption(String strNormalText,String key){
        String normalTextEnc=Values.EMPTY_STRING;
        try {
            normalTextEnc = Encrypt.encrypt(key, strNormalText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalTextEnc;
    }
    //metodo che data una stringa cifrata e una chiave ritorna una  stringa decifrata
    public static String decryption(String strEncryptedText,String key){
        String strDecryptedText=Values.EMPTY_STRING;
        try {
            strDecryptedText = Encrypt.decrypt(key, strEncryptedText);
        } catch (Exception e) {
            System.out.println(Values.ERROR);
            e.printStackTrace();
        }
        return strDecryptedText;
    }
    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance(AES);
        SecureRandom sr = SecureRandom.getInstance(SHA1PRNG, new CryptoProvider());
        sr.setSeed(seed);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }


    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw,AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }
    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length()/2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return Values.EMPTY_STRING;
        StringBuffer result = new StringBuffer(2*buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
    }

}

final class CryptoProvider extends Provider {
    //Classe che implementa un cryptoProvider, necessaria in quanto deprecato su android Nougat
    private final static String CRYPTO="Crypto";
    private final static String HARMONY = "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)";
    private final static String SHA1PRNG = "SecureRandom.SHA1PRNG";
    private final static String SECURERANDOMIMPL = "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl";
    private final static String IMPLEMENTEDIN = "SecureRandom.SHA1PRNG ImplementedIn";
    private final static String SOFTWARE = "Software";

    public CryptoProvider() {
        super(CRYPTO, 1.0, HARMONY);
        put(SHA1PRNG, SECURERANDOMIMPL);
        put(IMPLEMENTEDIN, SOFTWARE);
    }
}