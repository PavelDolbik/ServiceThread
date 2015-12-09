package com.example.pdolbik.callabletaskexecutor;

import java.util.concurrent.Executor;

/**
 * Created by p.dolbik on 09.12.2015.
 */
public class SimpleExecutor implements Executor {
    @Override
    public void execute(Runnable runnable) {
        new Thread(runnable).start();
    }
}
