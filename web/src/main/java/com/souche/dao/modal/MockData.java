package com.souche.dao.modal;

import com.souche.optimus.dao.annotation.SqlTable;

@SqlTable("mock_data")
public class MockData {
	Integer id;
	String key;
	String value;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
