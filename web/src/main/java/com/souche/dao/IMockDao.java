package com.souche.dao;

import com.souche.dao.modal.MockData;

public interface IMockDao {
	MockData get(String key);
	
	Long save(MockData mock);
	
	Integer delete(String key);
}
