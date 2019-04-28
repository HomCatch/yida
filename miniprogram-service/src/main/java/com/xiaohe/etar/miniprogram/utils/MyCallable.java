package com.xiaohe.etar.miniprogram.utils;

import java.util.concurrent.Callable;

public class MyCallable implements Callable<Integer> {
    private int i = 0;

    @Override
    public Integer call() {
        int sum = 0;
        for (; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + i);
            sum += i;
        }
        return sum;
    }
}