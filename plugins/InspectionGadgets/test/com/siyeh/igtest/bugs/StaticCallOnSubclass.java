package com.siyeh.igtest.bugs;

import com.siyeh.igtest.abstraction.StaticCallOnSubclassChild;

public class StaticCallOnSubclass {
                    
    public void foo() throws InterruptedException {
        Thread.sleep(1000L);
        InnerThread.sleep(1000L, 1000);
        StaticCallOnSubclassChild.foo();
    }
    
    class InnerThread extends Thread
    {

    }
}
