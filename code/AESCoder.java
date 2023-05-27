import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESCoder {
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String SKEY = "WEAVERECOLOGYSECURITY3.0VERSION201607150857";

    public AESCoder() {
    }

    public static byte[] initSecretKey(String code) {
        KeyGenerator kg = null;

        try {
            kg = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(code.getBytes());
            kg.init(128, secureRandom);
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
            return new byte[0];
        }

        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    private static Key toKey(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    public static InputStream encrypt(InputStream in, String code) throws Exception {
        byte[] key = initSecretKey(code);
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, k);
        CipherInputStream cis = new CipherInputStream(in, cipher);
        return cis;
    }

    public static OutputStream encrypt(OutputStream out, String code) throws Exception {
        byte[] key = initSecretKey(code);
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(1, k);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        return cos;
    }

    public static InputStream decrypt(InputStream in, String code) throws Exception {
        byte[] key = initSecretKey(code);
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(2, k);
        CipherInputStream cis = new CipherInputStream(in, cipher);
        return cis;
    }

    public static OutputStream decrypt(OutputStream out, String code) throws Exception {
        byte[] key = initSecretKey(code);
        Key k = toKey(key);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(2, k);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        return cos;
    }

    public static String decrypt(String sSrc, String sKey) throws Exception {
        try {
            if (sKey == null) {
                sKey = "WEAVERECOLOGYSECURITY3.0VERSION201607150857";
            }

            byte[] raw = initSecretKey(sKey);
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, skeySpec);
            byte[] encrypted1 = hex2byte(sSrc);

            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception var8) {
                var8.printStackTrace();
                return null;
            }
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            sKey = "WEAVERECOLOGYSECURITY3.0VERSION201607150857";
        }

        byte[] raw = initSecretKey(sKey);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(1, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return byte2hex(encrypted).toLowerCase();
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        } else {
            int l = strhex.length();
            if (l % 2 == 1) {
                return null;
            } else {
                byte[] b = new byte[l / 2];

                for(int i = 0; i != l / 2; ++i) {
                    b[i] = (byte)Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
                }

                return b;
            }
        }
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for(int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }

        return hs.toUpperCase();
    }

    public static String randomKey() {
        String keys = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int length = (int)(Math.random() * 16.0 + 16.0);
        int i = 0;
        String key = "";

        while(i < length) {
            int j = (int)(Math.random() * 61.0);
            if (j <= 61) {
                key = key + keys.substring(j, j + 1);
                ++i;
            }
        }

        return key;
    }

    public static void main(String[] args) throws Exception {
        //String encrypt = encrypt((String)"ecologyxindaoa@weaver.com.cn", (String)null);
        String encrypt = encrypt((String)"sysadmin1", (String)"1u6skkR");
        System.out.println(encrypt);
        //System.out.println(decrypt((String)encrypt, (String)null));
    }
}
