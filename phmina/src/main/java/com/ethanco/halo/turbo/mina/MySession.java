package com.ethanco.halo.turbo.mina;

/**
 * Created by ywr on 2021/9/6 9:53
 */
public class MySession  implements IMySession{
    @Override
    public long getId() {
        return -1000;
    }

    @Override
    public void write(Object message) {

    }

    @Override
    public void close() {

    }
}
