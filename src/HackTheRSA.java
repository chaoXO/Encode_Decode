

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class HackTheRSA {


    //因式分解
    public static int[] factor(int num) {
        if(num == 0) throw new IllegalArgumentException();
        if(num == 1) return new int[]{1};

        ArrayList<Integer> resList = new ArrayList<>();

        //核心算法
        for(int i = 1, max = num; i < max; i++){
            if(num % i == 0){
                int numi = num/i;
                resList.add(i);
                if(numi != i) resList.add(numi);
                max = numi;
            }
        }

        //ArrayList<Integer>转int[]
        int[] res = new int[resList.size()];
        for(int i = 0;i<resList.size();i++){
            res[i] = resList.get(i);
        }

        return res;
    }
    //欧几里得辗转相除法求逆 ed mod nn = 1 ，输出d
    public static int Euclid(int nn,int e){
        int[] m={1,0,nn};
        int[] n={0,1,e};
        int[] temp=new int[3];
        int q=0;  //初始化
        boolean flag=true;
        while(flag)
        {
            q=m[2]/n[2];
            for(int i=0;i<3;i++)
            {
                temp[i]=m[i]-q*n[i];
                m[i]=n[i];
                n[i]=temp[i];
            }
            if(n[2]==1)
            {
                if(n[1]<0)
                {
                    n[1]=n[1]+nn;
                }
                return n[1];
            }
            if(n[2]==0)
            {
                flag=false;
            }
        }
        return 0;
    }




    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {


        //注册BC包
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 设置生成30位RSAkey  0-私钥，1-公钥
        Map<Integer, String> map = KeyPairGeneratorRSA.getKeyPair(30);
        System.out.println(map.get(1));
        byte[] decoded = Base64.getDecoder().decode(map.get(1));
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA","BC").generatePublic(new X509EncodedKeySpec(decoded));
        byte[] decoded2 = Base64.getDecoder().decode(map.get(0));
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA","BC").generatePrivate(new PKCS8EncodedKeySpec(decoded2));
        System.out.println(pubKey.toString());
        System.out.println(priKey.toString());

        //计算耗时
        long l1 = System.currentTimeMillis();
        //得到公钥的modulus
        int[] list = HackTheRSA.factor(Integer.parseInt(pubKey.getModulus().toString()));
        for(int i:list){
            System.out.print(i+" ");
        }
        //求出RSA中的f(n)=(q-1)*(p-1)
        int fn = (list[2]-1) * (list[3]-1);
        System.out.println("fn ="+fn);
        int e = Integer.parseInt(pubKey.getPublicExponent().toString());
        System.out.println("e ="+e);
        // ed = x*fn+1 求d 暴力穷举
//        int[] list2=null;
//        find:
//        for(int x=0;;x++) {
//            list2 = HackTheRSA.factor(fn * x + 1);
//            for (int i : list2) {
//                if (i == e) break find;
//            }
//        }
//        for(int i:list2){
//            System.out.print(i+" ");
//        }

        //调用欧几里得辗转相除算法
        int d = HackTheRSA.Euclid(fn,e);
        System.out.println("d ="+d);

        long l2 = System.currentTimeMillis();
        System.out.println("破解RSA私钥用了"+(l2-l1)/1000/60+"分钟");
//        RSA_util rsa = new RSA_util();

    }
}
