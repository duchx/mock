package com.souche.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.souche.contextx.ServiceManager;
import com.souche.contextx.ServiceManager.ObjectHolder;
import com.souche.optimus.core.annotation.View;
import com.souche.optimus.core.web.Result;
import com.souche.test.ITestInterface;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "mock-api", description = "mockAPI")
@View
public class MockApi {

    @Autowired
    @Qualifier("testDubboInterface")
    private ITestInterface testInterface;

    @ApiOperation("mock")
    public Result<Object> mock(HttpServletRequest request,HttpServletResponse response,
    		@ApiParam("接口类(如:com.souche.test.ITestInterface)")String interfaceClass,
    		@ApiParam("方法(如:equals(java.lang.Object))")String method
    		) {
    	ObjectHolder serviceHolder = ServiceManager.get(interfaceClass);
    	if(serviceHolder==null)return Result.fail("500", "未发现服务");
    	Object service = serviceHolder.object;
    	for(Method m:service.getClass().getMethods()){
    		if(m.toGenericString().endsWith(method))
				try {
					return Result.success(m.invoke(service, new Object[m.getParameterTypes().length]));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					return Result.fail("500", "服务调用异常"+e.getMessage());
				}
    	}
    	return Result.fail("500", "未找到方法");
    }
    
    @ApiOperation("列举")
    public Result<List<String>> list(HttpServletRequest request,HttpServletResponse response) {
    	List<String> list = new ArrayList<String>();
    	ServiceManager.map().entrySet().forEach(s->{
    		for(Method method:s.getValue().interfaceClass.getMethods())
    			if(method.getDeclaringClass().hashCode()!=Object.class.hashCode())
    				list.add(method.toGenericString());
    	});
        return Result.success(list);
    }
}
