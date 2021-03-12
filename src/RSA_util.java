import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

//生成密钥对类
class KeyPairGeneratorRSA{
    public static Map<Integer, String> getKeyPair(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA","BC");
        // 初始化密钥对生成器，密钥大小一般为1024，或者2048
        keyPairGen.initialize(keySize,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        // 得到私钥字符串
        String privateKeyString =  Base64.getEncoder().encodeToString(privateKey.getEncoded());
        //  得到公钥字符串
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        Map<Integer,String> keyMap = new HashMap<>();
        //私钥放在0，公钥放在1
        keyMap.put(0,privateKeyString);
        keyMap.put(1,publicKeyString);
        return keyMap;
    }
}


public class RSA_util {

    //RSA 公钥加密
    public static String encryptBasedRSA(String data, String publickey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        // 公钥字符串转化为RSA公钥类型
        byte[] decoded = Base64.getDecoder().decode(publickey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA","BC").generatePublic(new X509EncodedKeySpec(decoded));
        // RSA公钥加密
        Cipher cipher = Cipher.getInstance("RSA","BC");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        // 数据用base64编码后RSA加密
        String encryptedData = new BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
        return encryptedData;
    }
    //RSA 私钥解密
    public static String decryptBasedRSA(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException {
        //私钥字符串转化成RSA私钥类型
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey privateKEY = (RSAPrivateKey) KeyFactory.getInstance("RSA","BC").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA 私钥解密
        Cipher cipher = Cipher.getInstance("RSA","BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKEY);
        // 数据用base64解码后RSA解密
        String decryptedData = new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(data)));
        return decryptedData;
    }



    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException, NoSuchProviderException {
        //注册BC包可以使用低于512位的RSA密钥
        Security.addProvider(new BouncyCastleProvider());
        //获取密钥对
        Map<Integer,String> map = KeyPairGeneratorRSA.getKeyPair(1024);
        //公钥加密
        System.out.println("用于加密的公钥是"+map.get(1));
        String str = "吃饭，睡觉，喝阔落";
        String str1 = encryptBasedRSA(str,map.get(1));
        System.out.println("公钥加密后的数据"+str1);
        //私钥解密
        String str2 = decryptBasedRSA(str1,map.get(0));
        System.out.println("解密后的数据："+str2);

    }
}
