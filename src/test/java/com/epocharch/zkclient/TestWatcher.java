package com.epocharch.zkclient;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by archer on 6/30/15.
 */
public class TestWatcher extends TestCase {

	public void testReconnectWatcher() throws InterruptedException {
		ZkClient zk = new ZkClient("10.161.144.77:2181", 10000);
		zk.subscribeChildChanges("/ttt", new IZkChildListener() {

			@Override
			public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
				System.out.println("trigger child change" + currentChilds);
			}
		});

		zk.subscribeDataChanges("/ttt", new IZkDataListener() {
			@Override
			public void handleDataChange(String dataPath, Object data) throws Exception {
				System.out.println("trigger data change");
			}

			@Override
			public void handleDataDeleted(String dataPath) throws Exception {
				System.out.println("trigger data delete");
			}
		});
		Thread.sleep(100000000);
	}
}
