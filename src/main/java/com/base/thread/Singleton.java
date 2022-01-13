package main.java.com.base.thread;

/*
  单例模式线程安全问题：双重校验锁实现对象单例
 */
public class Singleton {

    private static Singleton singleton;
    private Singleton(){

    }

    public static Singleton getInstance() throws InterruptedException {
        if(singleton==null){
            System.out.println("等待5秒...");
            Thread.sleep(5000);
            synchronized (Singleton.class){
                System.out.println(Thread.currentThread().getName()+"===当前线程");
                if(singleton==null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }


    public static void main(String args[]) {
        for (int i=0;i<10;i++){
            new Thread(new Runnable() {
                public void run() {
                    Singleton singleton = null;
                    try {
                        singleton = Singleton.getInstance();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName()+"===="+singleton);
                }
            }).start();
        }

    }

}
