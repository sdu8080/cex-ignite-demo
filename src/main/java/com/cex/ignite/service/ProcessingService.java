package com.cex.ignite.service;


public class ProcessingService {
	
	private Object payload;
	public ProcessingService(Object payload){
		this.payload = payload;
	}
	
	public Integer process(){
		String s = (String) payload;
		System.out.println("calling from server, length="+s.length());
		return s.length();
	}
}