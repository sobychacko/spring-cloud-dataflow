package org.springframework.cloud.dataflow.admin;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Activates the Spring Cloud Dataflow Admin Server.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AdminConfiguration.class)
public @interface EnableDataflowAdmin {
}
