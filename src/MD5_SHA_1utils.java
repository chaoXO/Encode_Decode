import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5_SHA_1utils {
    public static String getMD5String(String rawString, String algorithm) throws NoSuchAlgorithmException {
        char[] _16 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        MessageDigest md = MessageDigest.getInstance(algorithm);     //确定算法
        md.update(rawString.getBytes());                         //传入需要做摘要的字符串
        byte[] b = md.digest();                                  //生成 MD5：128bit的16位byte[]数组  SHA-1位 160位的20位数组
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i<b.length;i++) {
            sb.append(_16[b[i]>>4&0xf])         //取高四位对应的十六进制大小加入字符串 和f做&运算相当于和00001111做&运算
                    .append(_16[b[i]&0xf]);     //取低四位对应的十六进制大小加入字符串
        }
        return sb.toString();
    }
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String MD5 = "MD5";
        String sha_1 = "SHA-1";
        String raw = "吃饭睡觉喝可乐";
        String str = getMD5String(raw,MD5);
        System.out.println(raw+"的MD5摘要是\n"+str);
        String str2 = getMD5String(raw,sha_1);
        System.out.println(raw+"的SHA-1摘要是\n"+str2);
        if(getMD5String(raw,MD5).equals(str) && getMD5String(raw,sha_1).equals(str2)){
            System.out.println("固定算法和字符串产生摘要是一样的");
        }
    }

}


