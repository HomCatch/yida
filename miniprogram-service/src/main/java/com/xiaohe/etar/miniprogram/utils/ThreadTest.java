package com.xiaohe.etar.miniprogram.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author hzh
 * @version 1.0
 * @date 2019/3/25 10:01
 */
public class ThreadTest {

    public static void main(String[] args) {
        Callable<Integer> myCallable = new MyCallable();
        FutureTask<Integer> ft = new FutureTask<>(myCallable);
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + i);
            if (i == 30) {
                Thread thread = new Thread(ft);
                thread.start();
            }
        }
        System.out.println("主线程for循环结束");
        try {
            int sum = ft.get();
            System.out.println("sum=" + sum);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


}
