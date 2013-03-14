package me.sh4rewith.config.web;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import me.sh4rewith.utils.exceptions.AccessDeniedException;
import me.sh4rewith.web.dialect.Sh4reWithMeDialect;
import me.sh4rewith.web.interceptors.AppInfoExpositionInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.google.common.collect.Sets;

@EnableWebMvc
@Configuration
@ComponentScan("me.sh4rewith.web")
public class SpringWebMvcConfig extends WebMvcConfigurerAdapter {
	@Autowired
	Environment env;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(appInfoExpostionInterceptor());
	}

	@Bean
	public AppInfoExpositionInterceptor appInfoExpostionInterceptor() {
		return new AppInfoExpositionInterceptor();
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/welcome").setViewName("welcome");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/font/**").addResourceLocations("/font/");
		registry.addResourceHandler("/images/**").addResourceLocations("/images/");
	}

	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(JstlView.class);
		viewResolver.setPrefix("WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		viewResolver.setOrder(1);
		return viewResolver;
	}

	@Bean
	public TemplateResolver templateResolver() {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver();
		templateResolver.setPrefix("WEB-INF/thymeleaf/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		List<String> profiles = Arrays.asList(env.getActiveProfiles());
		if (profiles.contains("dev")) {
			templateResolver.setCacheable(false);
		}
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setOrder(2);
		return templateResolver;
	}

	@Bean
	public TemplateResolver emailResolver() {
		ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
		resolver.setPrefix("mailTemplates/");
		resolver.setTemplateMode("HTML5");
		resolver.setCharacterEncoding("UTF-8");
		resolver.setOrder(1);
		return resolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolvers(Sets.newHashSet(
				emailResolver(),
				templateResolver()
				));
		templateEngine.setMessageSource(messageSource());
		templateEngine.addDialect(new Sh4reWithMeDialect());
		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver thymeleafViewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setOrder(0);
		return viewResolver;
	}

	@Bean
	public MultipartResolver multipartResolver() {
		StandardServletMultipartResolver standardServletMultipartResolver = new StandardServletMultipartResolver();
		return standardServletMultipartResolver;
	}

	@Bean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages");
		return messageSource;
	}

	@Override
	public Validator getValidator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setValidationMessageSource(messageSource());
		return localValidatorFactoryBean;
	}

	@Bean
	public HandlerExceptionResolver exceptionResolver() {
		SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
		resolver.setDefaultErrorView("blameUs");
		Properties mappings = new Properties();
		mappings.put(AccessDeniedException.class.getName(), "forbidden");
		resolver.setExceptionMappings(mappings);
		return resolver;
	}

}