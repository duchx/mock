package com.souche.service;

public interface IMockService {
	String get(String methodName);
	
	Long save(String methodName, String returnValue);
	
	Integer delete(String methodName);
}
