package NIO_1;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * 一、缓冲区(Buffer):在Java NIO中负责数据的存取。缓冲区就是数组。用于存取不同数据类型的数据
 *
 *
 * 根据数据类型不同(boolean 除外)，提供了相应的缓冲区：
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * IntBuffer
 * LongBuffer
 * FloatBuffer
 * DoubleBuffer
 *
 *
 * 上述缓冲区的管理方式几乎一致，通过allocate获取缓冲区
 *
 * 二、缓冲区存取数据的两个核心方法：
 *  put():存入数据到缓冲区
 *  get():获取缓冲区的数据
 *
 *
 *  四、缓冲区中的四个核心属性：
 *  capacity：容量，表示缓冲区中最大存储数据的最大容量，一旦声明，不能改变
 *  limit：界限，表示缓冲区中可以操作数据的大小。（limit后面的数据不能进行读写）
 *  position：位置，表示缓冲区中正在操作数据的位置。  （   position <= limit <= capacity）
 *  mark()；标记，表示记录当前position的位置，可以通过reset()恢复到mark的位置。（  0 <= mark <= position）
 *
 *  五、直接缓冲区和非直接缓冲区
 *  非直接缓冲区：通过allocate()方法分配缓冲区，将缓冲区建立在JVM的内存中
 *  直接缓冲区：allocateDirect()方法分配直接缓冲区。将缓冲区建立在物理内存中。可以提高效率
 *
 *
 */


public class TestBuffer {

    @Test
    public void Test3(){
        // 分配直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
        System.out.println(buf.isDirect());
    }

    @Test
    public void Test2(){
        String str = "abcde";
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put(str.getBytes());
        buf.flip();

        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes,0,2);
        System.out.println(new String(bytes,0,2));
        System.out.println(buf.position());

        // mark()标记
        buf.mark();

        buf.get(bytes,2,2);
        System.out.println(new String(bytes,2,2));
        System.out.println(buf.position());

        buf.reset();// 恢复到mark的位置
        System.out.println(buf.position());

        // 判断缓冲区是否还有剩余的数据，
        if(buf.hasRemaining()){
            // 获取缓冲区中k可以操作的数量
            System.out.println(buf.remaining());   // 输出剩余数据的数据
        }

    }

    @Test
    public void Test1(){

        String s = "abcde";

        //1. 分配一个指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("---------------allocate()------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //2. 利用put()存入数据到缓冲区
        buf.put(s.getBytes());

        System.out.println("---------------put()------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        //3. flip()切换成读取数据的模式
        buf.flip();

        System.out.println("---------------flip()------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //4. 利用get()存入数据到缓冲区
        byte[] bytes = new byte[buf.limit()];
        buf.get(bytes);

        System.out.println("---------------get()------------------");
        System.out.println(new String(bytes,0,bytes.length));
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        //5.rewind()  // 可重复读数据
        buf.rewind();

        System.out.println("---------------rewind()------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        //6. clear():清空缓冲区.但是缓冲区中的数据依然存在，但是处于被遗忘状态
        buf.clear();

        System.out.println("---------------clear()------------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());
        System.out.println((char) buf.get());
    }




}
