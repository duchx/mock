package com.souche.contextx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
	private static Map<String, ObjectHolder> serviceHolders = new ConcurrentHashMap<>();
	
	public static void put(String key, ObjectHolder value){
		serviceHolders.put(key, value);
	}
	
	public static ObjectHolder get(String key){
		return serviceHolders.get(key);
	}
	
	public static Map<String, ObjectHolder> map(){
		return serviceHolders;
	}
	
	public static class ObjectHolder {
		public Class interfaceClass;
		public Object object;
		public ObjectHolder(Class interfaceClass, Object object){
			this.interfaceClass = interfaceClass;
			this.object = object;
		}
	}
}
