package NIO_2;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;

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

public class Non_BlockingNIO {

    // 客户端
    @Test
    public void Client() throws IOException {

        // 1.获取通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        // 2.切换成非阻塞模式
        socketChannel.configureBlocking(false);

        // 3.分配一个指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 4. 发送数据给服务端
        byteBuffer.put(new Date().toString().getBytes());
        byteBuffer.flip();

        socketChannel.write(byteBuffer);
        byteBuffer.clear();

        // 5.关闭通道
        socketChannel.close();


    }

    // 服务端
    @Test
    public void Server() throws IOException {

        // 1.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 2.切换成非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 3.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9898));

        // 4.获取选择器
        Selector selector = Selector.open();

        // 5.将通道注册到选择器上,并且指定"监听接收事件"
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 6.轮旋式地获取选择器上已经"准备就绪"地事件
        while (selector.select() > 0){
            // 7.获取当前选择器中所有注册的"选择键(已就绪的监听事件)"
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){
                // 8. 获取"准备就绪"的事件
                SelectionKey sk = iterator.next();

                // 9.判断具体是什么事件准备就绪
                if(sk.isAcceptable()){
                    // 10.若"接收就绪"，获取客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    // 11. 切换非阻塞
                    socketChannel.configureBlocking(false);

                    // 12. 将该通道注册到选择器上
                    socketChannel.register(selector,SelectionKey.OP_READ);

                }else if( sk.isReadable()){
                    // 13.获取当前选择器上"读就绪"的通道
                    SocketChannel channel = (SocketChannel) sk.channel();

                    // 14. 读取数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);

                    int len = 0;

                    while ((len = channel.read(buffer))!= -1){
                        buffer.flip();
                        System.out.println(new String(buffer.array(),0,len));
                        buffer.clear();
                    }
                }

                // 15.取消选择键(SelectionKey)
                iterator.remove();
            }
        }





    }


}
