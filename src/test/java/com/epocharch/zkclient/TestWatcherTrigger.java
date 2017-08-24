package com.epocharch.zkclient;

import junit.framework.TestCase;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by archer on 11/24/16.
 */
public class TestWatcherTrigger extends TestCase{

    public void testTriger() throws InterruptedException {
        String path = "/test/testTrigger";
        final AtomicInteger count = new AtomicInteger(0);
        ZkClient zk = new ZkClient("10.161.144.77:2181,10.161.144.78:2181,10.161.144.79:2181", 10000);
        if(!zk.exists(path)){
            zk.createPersistent(path);
        }
        zk.subscribeChildChanges(path,new IZkChildListener(){

            /**
             * Called when the children of the given path changed.
             *
             * @param parentPath    The parent path
             * @param currentChilds The children or null if the root node (parent path) was deleted.
             * @throws Exception
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(count.getAndIncrement()+":"+parentPath);
            }
        });
        ZkClient zkOther = new ZkClient("10.161.144.77:2181,10.161.144.78:2181,10.161.144.79:2181", 10000);
        String subPath = "/test/testTrigger/ephemeral";
        for(int i=0;i<50;i++){
            if(zkOther.exists(subPath)){
                zkOther.delete(subPath);
            }
            zkOther.createEphemeral(subPath);
        }
        Thread.sleep(1000000);
    }

    public void testConnectionSwitch() throws InterruptedException {
        String path = "/test/testTrigger";
        String subPath = "/test/testTrigger/ephemeral";
        final AtomicInteger count = new AtomicInteger(0);
        ZkClient zk = new ZkClient("10.161.144.77:2181,10.161.144.78:2181,10.161.144.79:2181", 10000);
        if(!zk.exists(path)){
            zk.createPersistent(path);
        }
        if(zk.exists(subPath)){
            zk.delete(subPath);
        }
        zk.subscribeChildChanges(path,new IZkChildListener(){

            /**
             * Called when the children of the given path changed.
             *
             * @param parentPath    The parent path
             * @param currentChilds The children or null if the root node (parent path) was deleted.
             * @throws Exception
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(currentChilds);
            }
        });
        ZkClient zk1 = new ZkClient("10.161.144.77:2181,10.161.144.78:2181,10.161.144.79:2181", 10000);

        zk1.createEphemeral(subPath,"aaa");
        ZkClient zk2 = new ZkClient("10.161.144.77:2181,10.161.144.78:2181,10.161.144.79:2181", 10000);
        if(zk2.exists(subPath)){
            zk2.writeData(subPath,"bbbb");
        }
        //zk2.createEphemeral(subPath);
        zk1.close();
        Thread.sleep(10000000);
    }
}
