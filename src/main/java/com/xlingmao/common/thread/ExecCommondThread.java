package com.xlingmao.common.thread;

import java.io.IOException;

public class ExecCommondThread implements Runnable{

	private String commond;
	
	public ExecCommondThread(){}
	
	public ExecCommondThread(String commond){
		this.commond = commond;
	}

	@Override
	public void run() {
		try {
			System.out.println(commond);
			Runtime.getRuntime().exec(commond);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
