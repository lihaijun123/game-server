package com.focus3d.game.client;

public class Test extends Thread{

	public static void main(String[] args) throws InterruptedException{
		for(int i = 0; i < 1; i ++){
			Test test = new Test();
			test.start();
		}
		Thread.sleep(1000000);
	}
	
	@Override
	public void run() {
		GameClient client = new GameClient();
		client.start("172.17.13.77", 8877);
		while(!client.isLogin){
		}
		client.sendData();
	}

	
}
