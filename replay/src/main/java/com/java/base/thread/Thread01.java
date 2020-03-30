package com.java.base.thread;

import org.junit.Test;
/*
  synchronized 用法：
（1）同步方法：修饰需要进行同步的普通方法，此时充当锁的对象为调用同步方法的对象；
     修饰静态方法：使用的锁是当前class字节码对象（类锁）
（2）：同步代码块：Synchronized（任意全局对象）{}
 */
public class Thread01 {

    private static int num=0;
    public Object object = new Object();

    public synchronized void a(){
        num++;
        System.out.println("a方法打印了！！！"+ num);
    }

    public static synchronized void b(){
        num++;
        System.out.println("b方法打印了！！！"+num);
    }

    public void c(){
        synchronized (object){
            num++;
            System.out.println("c方法打印了！！！"+num);
        }
    }


    @Test
    public void test() {
        final Thread01  thread01 = new Thread01();
        for (int i=0;i<10;i++){
            new Thread(new Runnable() {
                public void run() {
                    Thread01.b();
                }
            }).start();
        }
    }

}


