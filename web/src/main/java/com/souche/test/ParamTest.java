package com.souche.test;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ParamTest implements Serializable {
	private static final long serialVersionUID = 4753822097893520906L;
	private String a1;
	private Integer a2;
	private ParamTest2 a3;
	public String getA1() {
		return a1;
	}
	public void setA1(String a1) {
		this.a1 = a1;
	}
	public Integer getA2() {
		return a2;
	}
	public void setA2(Integer a2) {
		this.a2 = a2;
	}
	
	public ParamTest2 getA3() {
		return a3;
	}
	public void setA3(ParamTest2 a3) {
		this.a3 = a3;
	}
	public String toString(){
		return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
	}
}
