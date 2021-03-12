import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
//DES密钥生成类
class KeyGenerator{
    public static final byte[] getKEY_SEED(){
        SecureRandom random = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());
        byte[] bytes = new byte[60];
        random.nextBytes(bytes);
        return bytes;
    }

}
//加密解密类
public class DES_util {

    //DES加密函数
    public static String encryptBasedDes(String data,byte[] keys) {
        String encryptedData = null;
        try {
            //DES算法需要一个随机数发生器,如果不提供则调用JceSecurity.RANDOM
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(keys);
            // 把提供的密钥转化成规范的密钥(56位,补齐校验位后为64位)
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 加密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key,sr);
            // 加密，并把字节数组编码成字符串
            encryptedData = new BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("加密出错:", e);
        }
        return encryptedData;
    }
    //DES解密函数
    public static String decryptBasedDes(String cryptData,byte[] keys) {
        String decryptedData = null;
        try {
            // DES算法需要一个随机数发生器,如果不提供则调用JceSecurity.RANDOM
            SecureRandom sr = new SecureRandom();
            DESKeySpec deskey = new DESKeySpec(keys);
            // 把提供的密钥转化成规范的密钥(56位,补齐校验位后为64位)
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(deskey);
            // 解密对象
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key,sr);
            // 把字符串解码为字节数组，并解密
            decryptedData = new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(cryptData)));
        } catch (Exception e) {
            throw new RuntimeException("解密错误：", e);
        }
        return decryptedData;
    }

    public static void main(String[] args) {

        String str1="吃饭，睡觉，喝可乐";
        //生成密钥
        byte[] bytes = KeyGenerator.getKEY_SEED();
        // DES数据加密
        String s1=encryptBasedDes(str1,bytes);
        System.out.println("加密后的数据:"+s1);
        // DES数据解密
        String s2=decryptBasedDes(s1,bytes);
        System.out.println("解密后的数据:"+s2);
        //测试随机数发生器
//        SecureRandom random = new SecureRandom();
//        byte[] bytes = new byte[20];
//        random.nextBytes(bytes);
//        System.out.println(bytes[1]);

    }

}
