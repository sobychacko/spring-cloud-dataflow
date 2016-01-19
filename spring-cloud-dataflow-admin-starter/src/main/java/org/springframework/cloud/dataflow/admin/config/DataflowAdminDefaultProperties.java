package org.springframework.cloud.dataflow.admin.config;

import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * configures useful default properties for the local module
 * that may <em>still</em> be superseded using the conventional
 * Spring Boot mechanisms {@code --server.port}, {@code -Dserver.port=..}, etc.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public interface DataflowAdminDefaultProperties {
    void contributeDefaultProperties(ConfigurableEnvironment env,
                                     SpringApplication springApplication ,
                                     Map<String, Object> defaultsCollected);
}
