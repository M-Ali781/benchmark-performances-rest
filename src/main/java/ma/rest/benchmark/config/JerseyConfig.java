package ma.rest.benchmark.config;

import ma.rest.benchmark.controllers.jersey.JerseyCategoryResource;
import ma.rest.benchmark.controllers.jersey.JerseyItemResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(JerseyCategoryResource.class);
        register(JerseyItemResource.class);
    }
}
