package com.souche.contextx.parser;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.souche.contextx.ServiceBean;
import com.souche.contextx.scanner.ClassPathBeanDefinitionScanner;

public class ComponentScanBeanDefinitionParser extends org.springframework.context.annotation.ComponentScanBeanDefinitionParser {
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
        
		String basePackage = element.getAttribute("base-package");
		basePackage = parserContext.getReaderContext().getEnvironment().resolvePlaceholders(basePackage);
		String[] basePackages = StringUtils.tokenizeToStringArray(basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);

		// Actually scan for bean definitions and register them.
		ClassPathBeanDefinitionScanner scanner = (ClassPathBeanDefinitionScanner)configureScanner(parserContext, element);
		Set<BeanDefinitionHolder> beanDefinitions = scanner.doScan(basePackages);

		beanDefinitions = beanDefinitions.stream().filter(bdh->parserContext.getRegistry().containsBeanDefinition(bdh.getBeanName())).map(bdh->{
			RootBeanDefinition beanDefinition = new RootBeanDefinition();
			beanDefinition.setBeanClass(ServiceBean.class);
		    beanDefinition.setLazyInit(false);
		    beanDefinition.getPropertyValues().addPropertyValue("interface", bdh.getBeanDefinition().getBeanClassName());
		    parserContext.getRegistry().registerBeanDefinition(bdh.getBeanName(), beanDefinition);
		    return new BeanDefinitionHolder(beanDefinition, bdh.getBeanDefinition().getBeanClassName());
		}).collect(Collectors.toSet());
		
        registerComponents(parserContext.getReaderContext(), beanDefinitions, element);
		
		return null;
	}

	protected ClassPathBeanDefinitionScanner createScanner(XmlReaderContext readerContext, boolean useDefaultFilters) {
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(readerContext.getRegistry(), false);
		scanner.addIncludeFilter(new TypeFilter(){

			@Override
			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
					throws IOException {
				return true;
			}
			
		});
		return scanner;
	}
}
