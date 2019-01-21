package com.souche.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.souche.dao.IMockDao;
import com.souche.dao.modal.MockData;
import com.souche.service.IMockService;

@Service("MockServiceImpl")
public class MockServiceImpl implements IMockService {

	@Autowired
	@Qualifier("MockDaoImpl")
	IMockDao mockDao;
	
	@Override
	public String get(String methodName) {
		MockData mock = mockDao.get(methodName);
		return mock==null?null:mock.getValue();
	}

	@Override
	public Long save(String methodName, String returnValue) {
		MockData mock = new MockData();
		mock.setKey(methodName);
		mock.setValue(returnValue);
		return mockDao.save(mock);
	}

	@Override
	public Integer delete(String methodName) {
		return mockDao.delete(methodName);
	}

}
