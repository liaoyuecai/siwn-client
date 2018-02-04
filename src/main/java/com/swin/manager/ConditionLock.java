package com.swin.manager;

import com.swin.exception.ConditionTaskException;
import com.swin.exception.ConditionTimeoutException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by LiaoYuecai on 2018/1/26.
 */
public class ConditionLock {
    private static ConditionLock conditionLock = new ConditionLock();
    private Lock lock;
    private Map<String, Task> conditionMap;
    private Map<String, AsynchronousData> sysMap;


    private ConditionLock() {
        lock = new ReentrantLock();
        conditionMap = new ConcurrentHashMap<>();
        sysMap = new ConcurrentHashMap<>();
    }

    public static ConditionLock getInstance() {
        return conditionLock;
    }

    public Object await(String key, long timeout) throws Exception {
        if (sysMap.containsKey(key)) {
            AsynchronousData data = sysMap.get(key);
            sysMap.remove(key);
            return data.result;
        }
        try {
            if (conditionMap.containsKey(key)) {
                throw new ConditionTaskException("This task is exist");
            }
            Condition con = lock.newCondition();
            this.lock.lock();
            Task task = new Task(con);
            conditionMap.put(key, task);
            boolean flag = con.await(timeout, TimeUnit.MILLISECONDS);
            if (!flag) {
                throw new ConditionTimeoutException();
            }
            return task.result;
        } catch (Exception e) {
            throw e;
        } finally {
            conditionMap.remove(key);
            this.lock.unlock();
        }
    }

    public void release(String key, Object result) {
        if (conditionMap.containsKey(key)) {
            try {
                Condition con = conditionMap.get(key).con;
                conditionMap.get(key).result = result;
                this.lock.lock();
                con.signal();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.lock.unlock();
            }
        } else {
            sysMap.put(key, new AsynchronousData(result, System.currentTimeMillis()));
        }
    }

    class Task {
        private Condition con;
        private Object result;
        public Task(Condition con) {
            this.con = con;
        }
    }

    class AsynchronousData {
        private Object result;
        private long time;

        public AsynchronousData(Object result, long time) {
            this.result = result;
            this.time = time;
        }
    }
}
