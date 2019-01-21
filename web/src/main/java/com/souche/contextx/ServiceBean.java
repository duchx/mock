package com.souche.contextx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.base.Objects;
import com.souche.optimus.common.config.OptimusConfig;
import com.souche.service.IMockService;
import com.souche.test.ITestInterface;

public class ServiceBean extends com.alibaba.dubbo.config.spring.ServiceBean<Object> implements FactoryBean<Object> {

	private static final long serialVersionUID = 6565995602285317082L;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if(super.getRef()==null)this.getObject();
		super.afterPropertiesSet();
	}

	@Override
	public Object getObject() throws Exception {
		if(this.getRef()==null){
			IMockService mockService = (IMockService)getSpringContext().getBean("MockServiceImpl");
			this.setRef(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{getInterfaceClass()}, new ProxyHandler(mockService)));
			ServiceManager.put(this.getInterface(), new ServiceManager.ObjectHolder(this.getInterfaceClass(), this.getRef()));
		}
		return this.getRef();
	}

	@Override
	public Class getObjectType() {
		return getInterfaceClass();
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
	
	public static class ProxyHandler implements InvocationHandler {
		private IMockService mockService;

		public ProxyHandler(IMockService mockService){
			this.mockService = mockService;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if(method.getDeclaringClass().hashCode()==Object.class.hashCode())return method.invoke(this, args);
			//if(Objects.equal("equals", method.getName()))return this.equals(args);
			System.out.println(String.format("the method[%s] invoked", method.getName()));
			Boolean cacheEnabled = OptimusConfig.getValue("result.cached.enabled", Boolean.class);
			if(Objects.equal(cacheEnabled, Boolean.FALSE))
				return mock(method.getReturnType());
			
			String methodName = method.toGenericString().split(" ")[3];
			String retStr = mockService.get(methodName);
			Object ret = null;
			if(StringUtils.isEmpty(retStr)){
				ret = mock(method.getReturnType());
				mockService.save(methodName, JSON.toJSONString(ret, SerializerFeature.WriteClassName));
			}else{
				ret = JSON.parse(retStr);
			}
			return ret;
		}
		
		@SuppressWarnings("unchecked")
		public <T> T mock(Class<T> clazz){
			int hashCode = clazz.hashCode();

			if(hashCode == int.class.hashCode() || hashCode == Integer.class.hashCode()){
				return (T)Integer.valueOf((int)(Math.random()*10000));
			}else if(hashCode == double.class.hashCode() || hashCode == Double.class.hashCode())
				return (T)Double.valueOf((Math.random()*10000));
			else if(hashCode == float.class.hashCode() || hashCode == Float.class.hashCode())
				return (T)Float.valueOf((float)(Math.random()*10000));
			else if(hashCode == char.class.hashCode() || hashCode == Character.class.hashCode())
				return (T)Character.valueOf((char)(Math.floor(Math.random()*95)+31));
			else if(hashCode == String.class.hashCode()){
				char[] arr = new char[(int)Math.round(Math.random()*10)];
				for(int i=0;i<arr.length;i++){
					arr[i] = (char)(Math.floor(Math.random()*95)+31);
				}
				return (T)new String(arr);
			}else{
				try {
					T object = clazz.newInstance();
					for(Method method:clazz.getMethods()){
						if(method.getName().startsWith("set") && method.getParameterTypes().length==1){
							if(method.getParameterTypes()[0].hashCode()==clazz.hashCode())continue;
							method.invoke(object, mock(method.getParameterTypes()[0]));
						}
					}
					return object;
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		public  static void main(String[] a){
			ITestInterface aa = (ITestInterface) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{ITestInterface.class}, new ProxyHandler(null));
			System.out.println(aa.test(1, "d"));
			System.out.println(aa.test1(1));
			System.out.println(aa.test2(1));
		}
	}
}
