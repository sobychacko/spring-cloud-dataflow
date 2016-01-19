/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.admin.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataflowAdminDefaultEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered {


    private void collectContributionsFromAdminModules(ConfigurableEnvironment env,
                                                      SpringApplication springApplication,
                                                      Map<String, Object> defaultsCollected) {

        List<DataflowAdminDefaultProperties> defaultProperties = SpringFactoriesLoader.loadFactories(DataflowAdminDefaultProperties.class,
                org.springframework.util.ClassUtils.getDefaultClassLoader());

        for (DataflowAdminDefaultProperties d : defaultProperties)
            d.contributeDefaultProperties(env, springApplication, defaultsCollected);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env,
                                       SpringApplication springApplication) {

        Map<String, Object> defaults = new HashMap<>();

        MutablePropertySources existingPropertySources = env.getPropertySources();

        this.collectContributionsFromAdminModules(env, springApplication, defaults);

        String defaultProperties = "defaultProperties";

        if (!existingPropertySources.contains(defaultProperties) ||
                existingPropertySources.get(defaultProperties) == null) {
            existingPropertySources.addLast(new MapPropertySource(defaultProperties, defaults));
        } else {
            PropertySource<?> propertySource = existingPropertySources.get(defaultProperties);
            Map mapOfProperties = Map.class.cast(propertySource.getSource());
            for (String k : defaults.keySet())
                if (!mapOfProperties.containsKey(k))
                    mapOfProperties.put(k, defaults.get(k));
        }
    }

    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    }
}
