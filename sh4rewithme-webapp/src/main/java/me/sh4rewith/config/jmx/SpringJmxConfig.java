package me.sh4rewith.config.jmx;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;
import org.springframework.jmx.export.metadata.JmxAttributeSource;
import org.springframework.jmx.export.naming.MetadataNamingStrategy;
import org.springframework.jmx.export.naming.ObjectNamingStrategy;

@Configuration
public class SpringJmxConfig {
	@Autowired
	private Environment environment;

	@Bean
	public MBeanExporter metricsMbean() {
		MBeanExporter exporter = new MBeanExporter();
		exporter.setAssembler(assembler());
		exporter.setNamingStrategy(namingStrategy());
		exporter.setAutodetect(true);
		exporter.setRegistrationBehavior(MBeanExporter.REGISTRATION_IGNORE_EXISTING);
		return exporter;
	}

	@Bean
	public ApplicationMetrics metrics() {
		final Long updateRateTimeMillis = environment.getProperty("metrics.updateRateTimeMillis", Long.class);
		ApplicationMetrics metrics = new ApplicationMetrics();
		metrics.setLastUpdate(new Date());
		metrics.setUpdateRateTimeMillis(updateRateTimeMillis);
		return metrics;
	}

	@Bean
	public MBeanInfoAssembler assembler() {
		return new MetadataMBeanInfoAssembler(attributeSource());
	}

	@Bean
	public ObjectNamingStrategy namingStrategy() {
		MetadataNamingStrategy metadataNamingStrategy = new MetadataNamingStrategy();
		metadataNamingStrategy.setAttributeSource(attributeSource());
		return metadataNamingStrategy;
	}

	@Bean
	public JmxAttributeSource attributeSource() {
		return new AnnotationJmxAttributeSource();
	}
}
