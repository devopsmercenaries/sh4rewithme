package me.sh4rewith.config.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import me.sh4rewith.config.SpringAppConfig;
import me.sh4rewith.config.SpringEnvConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class Sh4reWithMeWebApplicationInitializer implements WebApplicationInitializer {
	final String[] DEFAULT_ACTIVE_PROFILES = new String[] { "demo", "registration-security" };

	@Override
	public void onStartup(ServletContext container) {
		// Create an application context for Config files (properties and such)
		AnnotationConfigApplicationContext envContext = createEnvContext();
		// Refresh this context in order to be able to use Spring environment properties
		// in the definition of servlets, filters and listeners.
		envContext.refresh();
		// Create the 'root' Spring application context
		AnnotationConfigWebApplicationContext appContext = createRootContext(envContext);

		// Manage the lifecycle of the root application context
		container.addListener(new ContextLoaderListener(appContext));

		// Create the dispatcher servlet's Spring application context
		AnnotationConfigWebApplicationContext dispatcherContext = createDispatcherContext(appContext);

		Environment environment = envContext.getEnvironment();
		// Register and map the dispatcher servlet
		registerDispatcherServlet(container, dispatcherContext, environment);

		// Add filter proxy for security
		registerSecurityFilter(container, environment);
	}

	private void registerSecurityFilter(ServletContext container, Environment rootEnvironment) {
		String securityFilterName = "org.springframework.security.filterChainProxy";
		FilterRegistration.Dynamic filter = container.addFilter(securityFilterName, new DelegatingFilterProxy(securityFilterName));
		filter.setInitParameter("targetBeanName", securityFilterName);
		EnumSet<DispatcherType> dispatchTypes = EnumSet.of(DispatcherType.REQUEST);
		filter.addMappingForUrlPatterns(dispatchTypes, false, rootEnvironment.getRequiredProperty("secured.pattern"));
	}

	private void registerDispatcherServlet(ServletContext container, AnnotationConfigWebApplicationContext dispatcherContext, Environment env) {
		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.setMultipartConfig(new MultipartConfigElement(
				env.getRequiredProperty("multipart.location"),
				env.getRequiredProperty("multipart.maxFileSize", Long.class),
				env.getRequiredProperty("multipart.maxRequestSize", Long.class),
				env.getRequiredProperty("multipart.fileSizeThreshold", Integer.class)
				));
		dispatcher.addMapping("/");
	}

	private AnnotationConfigWebApplicationContext createDispatcherContext(AnnotationConfigWebApplicationContext securityContext) {
		AnnotationConfigWebApplicationContext dispatcherContext = createBasicContext(securityContext);
		dispatcherContext.register(SpringWebMvcConfig.class);
		dispatcherContext.getEnvironment().setDefaultProfiles(DEFAULT_ACTIVE_PROFILES);
		return dispatcherContext;
	}

	private AnnotationConfigWebApplicationContext createRootContext(AnnotationConfigApplicationContext envContext) {
		AnnotationConfigWebApplicationContext rootContext = createBasicContext(envContext);
		rootContext.register(SpringAppConfig.class);
		rootContext.getEnvironment().setDefaultProfiles(DEFAULT_ACTIVE_PROFILES);
		return rootContext;
	}

	private AnnotationConfigApplicationContext createEnvContext() {
		AnnotationConfigApplicationContext envContext = new AnnotationConfigApplicationContext();
		envContext.register(SpringEnvConfig.class);
		envContext.getEnvironment().setDefaultProfiles(DEFAULT_ACTIVE_PROFILES);
		return envContext;
	}

	private static AnnotationConfigWebApplicationContext createBasicContext(ApplicationContext parentContext) {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		if (parentContext != null) {
			context.setParent(parentContext);
		}
		return context;
	}
}