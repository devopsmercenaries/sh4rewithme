package me.sh4rewith.config.jmx;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("jmxtrans")
@ImportResource("classpath:spring-jmxtrans.xml")
public class JmxTransConfig {

}
