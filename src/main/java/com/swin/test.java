package com.swin;

import com.swin.client.SwinClient;
import com.swin.open.SwinMapContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class test {
    public static void main(String[] args) throws Exception {
        SwinClient client = new SwinClient("myTest", SwinClient.SwinFeature.MAP, 20000);
        SwinMapContext context = (SwinMapContext) client.context("127.0.0.1", 2018);
        long a = System.currentTimeMillis();
        boolean flag = context.putMessage("myTest", "no1", new Person(1, "tome", 120));
        System.out.println(System.currentTimeMillis() - a);
         flag = context.putMessage("myTest", "no1", new Person(2, "tome", 120));
         flag = context.putMessage("myTest", "no1", new Person(2, "tome", 120));
         flag = context.putMessage("myTest", "no1", new Person(2, "tome", 120));
        System.out.println(System.currentTimeMillis() - a);
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        context.getMessage("myTest", "no1");
        System.out.println(System.currentTimeMillis() - a);
    }
}
