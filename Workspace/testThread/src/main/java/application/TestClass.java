package application;

import vo.A;
import vo.A_0;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwens on 2017/12/19.
 */
public  class  TestClass{

    public void add( <? extends A>)
    {
        List<? extends A> test = new ArrayList<>();

        test.add(new A_0());
        test.add(new A_0());

        Object a_0 = test.get(0);

        A_0 a_0 = new A();

    }


}