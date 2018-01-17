package application;

import threads.MyThread;

/**
 * Created by liuwens on 2017/12/18.
 */
public class TestThread
{

    //local branch dev 001

    // test on dev

    //22222


    public static void main(String[] args)
    {

        TestThread testThread = new TestThread();

        MyThread mt = new MyThread();

        Thread th1=new Thread(mt,"窗口1");
        Thread th2=new Thread(mt,"窗口2");
        Thread th3=new Thread(mt,"窗口3");


        //启动三个线程，也即是三个窗口，开始卖票
        th1.start();
        th2.start();
        th3.start();



    }

    private void test()
    {
        /*Runnable runnable = () -> {

            int totalTickNum = 100;

            synchronized (Integer.valueOf(totalTickNum)){

                while(true)
                {
                    if(totalTickNum < 0)
                    {
                        break;
                    }
                    totalTickNum --;

                    System.out.printf("当前是第[%d]张票\n", totalTickNum);
                }
            }

        };*/

        MyThread mt = new MyThread();

        Thread th1=new Thread(mt,"窗口1");
        Thread th2=new Thread(mt,"窗口2");
        Thread th3=new Thread(mt,"窗口3");


        //启动三个线程，也即是三个窗口，开始卖票
        th1.start();
        th2.start();
        th3.start();

    }



}
