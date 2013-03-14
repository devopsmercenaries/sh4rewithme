package me.sh4rewith.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({
		"me.sh4rewith.config.jmx",
		"me.sh4rewith.config.mail",
		"me.sh4rewith.config.persistence",
		"me.sh4rewith.config.security",
		"me.sh4rewith.domain",
		"me.sh4rewith.service",
		"me.sh4rewith.persistence",
		"me.sh4rewith.persistence.mongo" })
@EnableScheduling
public class SpringAppConfig {
	@Autowired
	private Environment env;

	@Bean(name = "appVersion")
	public String appVersion() {
		String appVersion = env.getProperty("app.version", String.class, "vx.x.x");
		appVersion = appVersion.equals("${project.version}") ? "vlocal.dev.version" : appVersion;
		return "v"+appVersion;
	}
}