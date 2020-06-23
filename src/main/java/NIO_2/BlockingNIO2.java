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

public class BlockingNIO2 {



    // 客户端
    @Test
    public void Client() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

        FileChannel open = FileChannel.open(Paths.get("src//main//java//Channel//0450411F4-15.jpg"), StandardOpenOption.READ);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (open.read(byteBuffer) != -1){
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        socketChannel.shutdownOutput();

        // 接收反馈
        int len = 0;
        while ((len = socketChannel.read(byteBuffer)) != -1){
            byteBuffer.flip();
            System.out.println(new String(byteBuffer.array(),0,len));
            byteBuffer.clear();
        }

        socketChannel.close();
        open.close();

    }

    // 服务端
    @Test
    public void Server() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.bind(new InetSocketAddress(9898));

        FileChannel open = FileChannel.open(Paths.get("src//main//java//NIO_2//02.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);


        SocketChannel socketChannel = serverSocketChannel.accept();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (socketChannel.read(byteBuffer) != -1){
            byteBuffer.flip();
            open.write(byteBuffer);
            byteBuffer.clear();
        }



        // 发送发聩给客户端
        byteBuffer.put("服务端接收数据成功".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        socketChannel.shutdownOutput();
        serverSocketChannel.close();
        socketChannel.close();
        open.close();

    }


}
