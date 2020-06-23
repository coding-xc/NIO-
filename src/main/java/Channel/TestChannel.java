package Channel;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 *  一、通道(Channel)：用于源节点与目标节点的连接。在Java NIO中负责缓冲区中数据的传输。Channel 本身不存储数据，因此需要配合缓冲区进行传输
 *
 *  二、通道的主要实现类
 *  Java.nio.channels.Channel 接口：
 *      -- FileChannel:
 *      -- SocketChannel:
 *      -- ServerSocketChannel:
 *      -- DatagramChannel:
 *  三、获取通道
 *  1.Java针对支持通道的类提供了getChannel()方法
 *      本地IO：
 *      FIleInputStream/FileOutputStream
 *      RandomAccessFile
 *
 *      网络：
 *      Socket
 *      ServerSocket
 *      DatagramSocket
 *
 *  2.在JDK1.7中的NIO.2中针对各个通道提供了一个静态方法 open()
 *
 *  3.在JDK1.7中的NIO.2的Files工具类的newByteChannel()
 *
 *  4.通道之间的数据传输
 *    transferFrom()
 *    transferTo()
 *
 *  五、分散(Scatter)与聚集(Gather)
 *  分散读取：(Scatter Reads):将通道中的数据分散到多个缓冲区中
 *  聚集写入:(Gathering Writes):将多个缓冲区的数据聚集到通道中
 *
 *  六、字符集
 *  编码：字符串->字节数组
 *  解码：字节数组->字符串
 *
 */

public class TestChannel {

    // 字符集
    @Test
    public void Test6() throws CharacterCodingException {
        Charset gbk = Charset.forName("GBK");


        // 获取编码器
        CharsetEncoder charsetEncoder = gbk.newEncoder();


        // 获取解码器
        CharsetDecoder charsetDecoder = gbk.newDecoder();

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("sdfsdf");
        charBuffer.flip();

        // 编码
        ByteBuffer encode = charsetEncoder.encode(charBuffer);
        for(int i=0;i<6;i++){
            System.out.println(encode.get());
        }
        // 解码
        encode.flip();
        CharBuffer decode = charsetDecoder.decode(encode);
        System.out.println(decode.toString());

    }


    @Test
    public void Test5(){
        SortedMap<String, Charset> map = Charset.availableCharsets();


        Set<Map.Entry<String, Charset>> entries = map.entrySet();
        for(Map.Entry<String, Charset> entry:entries){
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

    }

    // 5. 分散和聚集
    @Test
    public void Test4() throws IOException {
        RandomAccessFile raf1 = new RandomAccessFile("src//main//java//Channel//001.txt","rw");


        // 1. 获取通道
        FileChannel channel = raf1.getChannel();

        // 2. 分配指定大小的缓冲区
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(100);
        ByteBuffer byteBuffer2 = ByteBuffer.allocate(1024);

        // 3. 分散读取
        ByteBuffer[] byteBuffers = {byteBuffer1,byteBuffer2};
        channel.read(byteBuffers);
        for(ByteBuffer byteBuffer:byteBuffers){
            byteBuffer.flip();

        }

        System.out.println(new String());

    }

    // 3.通道之间的数据传输(直接缓冲区)
    @Test
    public void Test3() throws IOException {
        FileChannel in = FileChannel.open(Paths.get("src//main//java//Channel//0450411F4-15.jpg"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("src//main//java//Channel//04.jpg"),StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW,StandardOpenOption.READ);

//        in.transferTo(0,in.size(),out);
        out.transferFrom(in,0,in.size());
        in.close();
        out.close();

    }


    // 2.使用直接缓冲区完成文件的复制(内存映射文件)
    @Test
    public void Test2() throws IOException {

        long Start = System.currentTimeMillis();
        FileChannel in = FileChannel.open(Paths.get("src//main//java//Channel//0450411F4-15.jpg"), StandardOpenOption.READ);
        FileChannel out = FileChannel.open(Paths.get("src//main//java//Channel//03.jpg"),StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW,StandardOpenOption.READ);

        // 内存映射文件(只有 ByteBuffer 支持)
        MappedByteBuffer map = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
        MappedByteBuffer map1 = out.map(FileChannel.MapMode.READ_WRITE, 0, in.size());

        byte[] bys = new byte[map.limit()];
        // 直接对缓冲区进行数据的读写操作
        map.get(bys);
        map1.put(bys);

        in.close();
        out.close();

        long End = System.currentTimeMillis();
        System.out.println("耗费的时间为："+ (End-Start));

    }


    // 1.利用通道完成文件的复制(非直接缓冲区)
    @Test
    public void Test1() {
        long Start = System.currentTimeMillis();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel inchannel = null;
        FileChannel outchannel = null;
        try {
            fis = new FileInputStream("src//main//java//Channel//0450411F4-15.jpg");
            fos = new FileOutputStream("src//main//java//Channel//02.jpg");



            // ① 获取通道
            inchannel = fis.getChannel();
            outchannel = fos.getChannel();

            // ② 分配一个指定大小的缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            // ③ 将通道中的数据存入缓冲区中
            while (inchannel.read(byteBuffer) != -1){
                byteBuffer.flip();  // 切换成读取数据的模式
                // ④ 将缓冲区中的数据写入通道
                outchannel.write(byteBuffer);
                byteBuffer.clear(); // 清空缓冲区
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(outchannel != null){
                try {
                    outchannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inchannel != null){
                try {
                    inchannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long End = System.currentTimeMillis();
        System.out.println("耗费的时间为："+ (End-Start));

    }


}
