package org.springframework.cloud.dataflow.admin.spi.local;


import org.springframework.boot.SpringApplication;
import org.springframework.cloud.dataflow.admin.config.DataflowAdminDefaultProperties;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

public class LocalAdminDefaultProperties
        implements DataflowAdminDefaultProperties {

    @Override
    public void contributeDefaultProperties(ConfigurableEnvironment env,
                                            SpringApplication springApplication,
                                            Map<String, Object> defaultsCollected) {
        defaultsCollected.put("server.port", Long.toString(9393));
        defaultsCollected.put("management.contextPath", "/management");
        defaultsCollected.put("spring.application.name", "spring-cloud-dataflow-admin-local");
        defaultsCollected.put("security.basic.enabled", Boolean.toString(false));

    }
}
