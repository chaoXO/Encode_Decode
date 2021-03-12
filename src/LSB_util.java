

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;


class LSB{
    public BufferedImage bfi;
    public InputStream idata;
    public OutputStream ops;

    //可适当调整大小
    int[] rgb;
    LSB(BufferedImage bfi,InputStream data,OutputStream ops){
        this.bfi = bfi;
        this.idata = data;
        this.ops = ops;
    }
    boolean encodeDataImage() throws IOException {
            int width = bfi.getWidth();
            int height = bfi.getHeight();
            int sum = idata.available();
            //判断图片大小是否能够隐写足够的数据
            if (height*width<sum) return false;
            String temp = "";
            while (idata.available() > 0){
                //读取一个字节转化位二进制字符串“1010101010”
                int k = idata.read();
                for(int i = 7;i >= 0;i--)
                    if((k & (1 << i)) == 0) temp += "0"; //填充前面的0
                    else break;
                // Integer.toBinaryString()在转化成二进制的字符串的时候不保留前面填充的0，匹配填充
                temp += Integer.toBinaryString(k);
            }
            //把16个0的字符串标识为魔数放在末尾 代表信息的结束
            temp += "0000000000000000";
            while(temp.length()%3 != 0) temp += "0";
            System.out.println(temp + "加密前的长度" +temp.length());
            //获得RGB色彩模型  使用图像中的指定坐标（x，y），可以以这种方式访问ARGB像素：
            // pixel = rgbArray[offset + (y-startY)*scansize + (x-startX)] 看出扫描方式为左到右，上到下
            rgb = new int[height*width];  //给rgb数组设定容量 不然会报越界异常
            rgb = bfi.getRGB(0,0,width,height,rgb,0,width);
            for (int i = 0; i < height;i++)
                for (int j = 0; j < width;j++) {
                    int pos = i * width  + j;
                    if (pos*3 < temp.length()){
                        rgb[pos] &= (0xffffffff-0x010101);//利用ARGB的RGB通道进行加密，先把RGB的最低位都置0
                        int num = ((temp.charAt(pos*3 + 0) == '1')?(1 << 16):0) //R通道最低位
                                + ((temp.charAt(pos*3 + 1) == '1')?(1 << 8):0)    //G通道最低位
                                + ((temp.charAt(pos*3 + 2) == '1')?(1):0);       //B通道最低位
                        rgb[pos] |= num;
                    }
                }
            System.out.println();
            bfi.setRGB(0,0,width,height,rgb,0,width);
            return true;
    }
    boolean decodeDataImage() throws IOException {
        int width = bfi.getWidth();
        int height = bfi.getHeight();
        String temp = "";
        rgb = bfi.getRGB(0,0,width,height,rgb,0,width);
        int magic_cont = 0;
        magic:
        for (int i = 0;i < height;i++)
            for (int j=0;j < width;j++){
                int pos = i * width + j;
                for (int k = 16; k >=0 ; k -= 8){    //循环3次分别获取RGB通道的最低位
                    if(( rgb[pos] & (1 << k)) != 0){
                        temp += "1";
                        magic_cont = 0;
                    }else {
                        temp += "0";
                        if ((++magic_cont) >= 16) break magic;
                    }
                    if (temp.length() == 8){
                        int out = Integer.parseInt(temp,2); //radix:以二进制位基数，默认是使用以十进制
                        System.out.print(temp+"");
//                        System.out.print(out+" ");
                        ops.write(out);
                        temp = "";
                    }
                }
            }
        return true;
    }



}






public class LSB_util{
    public static void main(String[] args) throws IOException {

        //绝对路径的写法
//        BufferedImage bfi= ImageIO.read(new FileInputStream("D:\\IDEA_JAVA\\Encode_Decode\\src\\01.jpg"));
       //相对路径的写法  创建图片输出流(用来加密的图片)
        BufferedImage bfi= ImageIO.read(new FileInputStream("src\\01.jpg"));
        //输出传入照片的高和宽
        System.out.println(bfi.getHeight());
        System.out.println(bfi.getWidth());
        //创建文本输入流和文本输出流
        InputStream ips = new BufferedInputStream(new FileInputStream("src\\plain.txt"));
        OutputStream ops = new BufferedOutputStream(new FileOutputStream("src\\plain2.txt"));
        //创建LSB对象传入文本流和图片流
        LSB lsb = new LSB(bfi,ips,ops);
        lsb.encodeDataImage();
        //读取已经加密好的图片文件
        ImageIO.write(lsb.bfi,"jpg",new FileOutputStream("src\\02.jpg"));
        //读取已经LSB隐写加密好的的图片
        BufferedImage bfi2= ImageIO.read(new FileInputStream("src\\02.jpg"));
        //把读取加密好的图片传给LSB的对象
//        lsb.bfi = bfi2;
        lsb.decodeDataImage();

//        System.out.println();
        //        int b;
//        while ((b = ips.read())!= -1){
//            ops.write(b);
//        }
        ips.close();
        ops.close();
        return;
    }

}
