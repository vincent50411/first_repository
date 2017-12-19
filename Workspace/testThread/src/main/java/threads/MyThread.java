package threads;

/**
 * Created by liuwens on 2017/12/18.
 */
public class MyThread implements Runnable
{
    int totalTickNum = 5;

    public void run()
    {
        synchronized (Integer.valueOf(totalTickNum))
        {
            while(true)
            {
                if(totalTickNum < 1)
                {
                    break;
                }

                System.out.printf(this.toString() + "; 当前是第[%d]张票\n", totalTickNum);

                totalTickNum --;
            }
        }
    }
}
