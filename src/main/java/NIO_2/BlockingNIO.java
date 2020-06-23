package NIO_2;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 *  一、使用NIO完成网络通信的三个核心：
 *  1.通信(Channel)：负责连接
 *      java.nio.channels.Channel 接口：
 *        -- SelectableChannel
 *        -- ServerSocketChannel
 *        -- DataGramChannel
 *
 *        -- Pipe.SinkChannel
 *        -- Pipe.SourceChannel
 *
 *  2.缓冲区(Buffer)：负责数据的存取
 *
 *
 *  3.选择器(Selector)：是 SelectableChannel的多路复用器。用于监控SelectableChannel的IO状况
 *
 *
 */
public class BlockingNIO {

    // 客户端
    @Test
    public void Client() throws IOException {

        // 1. 获取通道
        SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        // 2. 分配指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        FileChannel open = FileChannel.open(Paths.get("src//main//java//Channel//0450411F4-15.jpg"), StandardOpenOption.READ);

        // 3. 读取本地文件并发送到服务端
        while (open.read(byteBuffer) != -1){
            byteBuffer.flip();
            channel.write(byteBuffer);
            byteBuffer.clear();
        }



        // 4. 关闭通道
        open.close();
        channel.close();

    }


    // 服务端
    @Test
    public void Server() throws IOException {

        // 1. 获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2. 绑定连接端口号
        serverSocketChannel.bind(new InetSocketAddress(9898));

        FileChannel open = FileChannel.open(Paths.get("src//main//java//NIO_2//01.jpg"),StandardOpenOption.WRITE,StandardOpenOption.CREATE,StandardOpenOption.READ);

        // 3. 获取客户端连接的通道
        SocketChannel accept = serverSocketChannel.accept();

        // 4. 分配一个指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 5. 接收客户端的数据，并保存到本地
        while (accept.read(byteBuffer) != -1){
            byteBuffer.flip();
            open.write(byteBuffer);
            byteBuffer.clear();
        }

        // 6. 关闭通道
        serverSocketChannel.close();
        open.close();
        accept.close();

    }

}
