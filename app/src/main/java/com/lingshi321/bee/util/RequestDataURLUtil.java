package com.lingshi321.bee.util;

import java.util.Map;

import com.lingshi321.bee.LoginActivity;
import com.lingshi321.bee.common.LoginRunnable;
import com.lingshi321.bee.common.MyOrderDataRunnable;

import android.content.Context;
import android.os.Handler;

public class RequestDataURLUtil {

    private static boolean isFirst = false;
    private static Thread[] threads = new Thread[2];
    private static MyOrderDataRunnable[] tasks = new MyOrderDataRunnable[2];

    public static void getOrdersURL(final Handler handler, Context context, final int orderStatus, final int pageNumber, final int pageSize) {
        Runnable task = new MyOrderDataRunnable(handler, context, orderStatus, pageNumber, pageSize);
        startTask(task);
    }

    public static void login(final Handler handler, final Map<String, String> params, LoginActivity loginActivity) {
        Runnable task = new LoginRunnable(handler, params, loginActivity);
        startTask(task);
    }

    /**
     * 启动任务，并且是两个线程交替使用，如果其中
     * 每一个线程正在运行，就把它强制结束，创建一
     * 个新的线程
     *
     * @param task
     */
    private static void startTask(Runnable task) {
        if (!isFirst) {
            if (threads[0] == null) {
                threads[0] = new Thread(task);
                tasks[0] = (MyOrderDataRunnable) task;
                threads[0].start();
                isFirst = true;
            } else {
                if (threads[0].isAlive()) {
                    tasks[0].stopThisThread();
                    try {
                        threads[0].interrupt();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }
                threads[0] = new Thread(task);
                tasks[0] = (MyOrderDataRunnable) task;
                threads[0].start();
                isFirst = true;
            }
        } else {
            if (threads[1] == null) {
                threads[1] = new Thread(task);
                tasks[1] = (MyOrderDataRunnable) task;
                threads[1].start();
                isFirst = false;
            } else {
                if (threads[1].isAlive()) {
                    tasks[1].stopThisThread();
                    try {
                        threads[1].interrupt();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
                threads[1] = new Thread(task);
                tasks[1] = (MyOrderDataRunnable) task;
                threads[1].start();
                isFirst = false;
            }
        }
    }
}
