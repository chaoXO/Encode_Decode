import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;


//报错：java.security.InvalidKeyException: Illegal key size or default parameters
//jdk 或jre\lib\security目录 下, 需要两个保密的jar文件
//Oracle在其官方网站上提供了无政策限制权限文件（Unlimited Strength Jurisdiction Policy Files），我们只需要将其部署在JRE环境中，就可以解决限制问题。
//http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-java-plat-419418.html
//把里面的两个jar包：local_policy.jar 和 US_export_policy.jar 替换掉原来安装目录jdk中或者jre中的\lib\security 下的两个jar包接可以了


class KeyPairGeneratorECC {
    //注册BouncyCastle加密包
    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    public static KeyPair getGenerateKey() throws NoSuchProviderException, NoSuchAlgorithmException {

        //确认算法EC， BC即BouncyCastle加密包，EC为ECC算法  BouncyCastleProvider.PROVIDER_NAME等同 “BC”
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
        //初始化 key用256位
        keyPairGenerator.initialize(256, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
//        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        return keyPair;
    }
}

public class ECC_util {
    public static String encryptBasedECC(String data, ECPublicKey publickey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException {
        //进行ECC加密
        Cipher cipher = Cipher.getInstance("ECIES","BC");
        cipher.init(Cipher.ENCRYPT_MODE, publickey);
        String encryptedData = new BASE64Encoder().encode(cipher.doFinal(data.getBytes()));
        return encryptedData;
    }

    public static String decryptBasedECC(String data, ECPrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        //进行ECC解密
        Cipher cipher = Cipher.getInstance("ECIES","BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String decryptedData = new String(cipher.doFinal(new BASE64Decoder().decodeBuffer(data)));
        return decryptedData;
    }



    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidKeyException, NoSuchPaddingException, IOException {
        KeyPair keyPair = KeyPairGeneratorECC.getGenerateKey();
        System.out.println("生成的私钥是"+new String(keyPair.getPrivate().getEncoded()));
        System.out.println("生成的公钥是"+new String(keyPair.getPublic().getEncoded()));
        //用来加密的文本
        String str = "ECC椭圆加密的明文";
        //加密
        String str1 = encryptBasedECC(str,(ECPublicKey) keyPair.getPublic());
        System.out.println("加密后的密文是");
        System.out.println(str1);
        //解密
        String str2 = decryptBasedECC(str1,(ECPrivateKey)keyPair.getPrivate());
        System.out.println("解密后的明文是");
        System.out.println(str2);
    }

}





































