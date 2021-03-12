import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
//AES密钥生成类
class KeyPairGeneratorAES {
    public static final SecretKey getKEY_SEED(int KeyBit) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());
        byte[] bytes = new byte[128];
        random.nextBytes(bytes);
        // 把提供的密钥转化成规范的密钥
        SecureRandom sr = new SecureRandom();
        sr.setSeed(bytes);
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        //设定密码的位数
        generator.init(KeyBit, sr);
        SecretKey secKey = generator.generateKey();
        return secKey;
    }
}
//加密解密类
public class AES_util {
    public static String encryptBasedAes(String data,SecretKey key,int KeyBit) {
        String encryptedData = null;
        try {
            // AES算法需要一个随机数发生器,如果不提供则调用JceSecurity.RANDOM
            SecureRandom sr = new SecureRandom();
            //加密对象
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key,sr);
            // 把字符串解码为字节数组，并加密  把结果转换成十六进制的String
            // byte[] 和 二进制String之间不能可逆的互转
            byte[] temp = cipher.doFinal(data.getBytes());
            encryptedData = parseByte2HexStr(temp);
        } catch (Exception e) {
            throw new RuntimeException("加密出错:", e);
        }
        return encryptedData;
    }

    //解密函数
    public static String decryptBasedAes(String cryptData,SecretKey key,int KeyBit) {
        String decryptedData = null;
        try {
            // AES算法需要一个随机数发生器,如果不提供则调用JceSecurity.RANDOM
            SecureRandom sr = new SecureRandom();
            //加密对象
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key,sr);
            // 把字符串解码为字节数组，并解密
            byte[] secCryptDAte = parseHexStr2Byte(cryptData);
            decryptedData = new String(cipher.doFinal(secCryptDAte));
        } catch (Exception e) {
            throw new RuntimeException("解密错误：", e);
        }
        return decryptedData;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {

        String str1="吃饭，睡觉，喝可乐";
        //生成密钥 设置AES密钥位数 128/192/256 bit
        int keybit = 128;
        SecretKey key = KeyPairGeneratorAES.getKEY_SEED(keybit);
        // AES数据加密
        String s1=encryptBasedAes(str1,key,keybit);
        System.out.println("加密后的数据:"+s1);
        // AES数据解密
        String s2=decryptBasedAes(s1,key,keybit);
        System.err.println("解密后的数据:"+s2);
        //测试随机数发生器
//        SecureRandom random = new SecureRandom();
//        byte[] bytes = new byte[20];
//        random.nextBytes(bytes);
//        System.out.println(bytes[1]);

    }

    //二进制转16进制
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    //十六进制转二进制
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}



