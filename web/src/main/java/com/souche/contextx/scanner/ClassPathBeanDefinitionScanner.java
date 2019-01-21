package com.souche.contextx.scanner;

import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class ClassPathBeanDefinitionScanner extends org.springframework.context.annotation.ClassPathBeanDefinitionScanner {

	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		super(registry, useDefaultFilters);
	}

	public Set<BeanDefinitionHolder> doScan(String... basePackages){
		return super.doScan(basePackages);
	}
	
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface();
	}
}
