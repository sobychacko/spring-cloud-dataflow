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

package org.springframework.cloud.dataflow.admin;

import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

import java.util.Arrays;

import org.springframework.boot.actuate.metrics.repository.MetricRepository;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.dataflow.admin.completion.TapOnChannelExpansionStrategy;
import org.springframework.cloud.dataflow.admin.config.ArtifactRegistryPopulator;
import org.springframework.cloud.dataflow.admin.controller.WebConfiguration;
import org.springframework.cloud.dataflow.admin.repository.*;
import org.springframework.cloud.dataflow.artifact.registry.ArtifactRegistry;
import org.springframework.cloud.dataflow.artifact.registry.RedisArtifactRegistry;
import org.springframework.cloud.dataflow.completion.CompletionConfiguration;
import org.springframework.cloud.dataflow.completion.RecoveryStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration for admin application context. This includes support
 * for the REST API framework configuration.
 *
 * @author Mark Fisher
 * @author Marius Bogoevici
 * @author Patrick Peralta
 * @author Thomas Risberg
 * @author Janne Valkealahti
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Configuration
@ConfigurationProperties
@EnableConfigurationProperties(AdminProperties.class)
@Import({CompletionConfiguration.class, RepositoryConfiguration.class, WebConfiguration.class})
public class AdminConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MetricRepository metricRepository(RedisConnectionFactory redisConnectionFactory) {
        return new RedisMetricRepository(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public StreamDefinitionRepository streamDefinitionRepository() {
        return new InMemoryStreamDefinitionRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskDefinitionRepository taskDefinitionRepository() {
        return new InMemoryTaskDefinitionRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public ArtifactRegistry artifactRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisArtifactRegistry(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public ArtifactRegistryPopulator artifactRegistryPopulator(ArtifactRegistry artifactRegistry) {
        return new ArtifactRegistryPopulator(artifactRegistry);
    }


    @Bean
    @ConditionalOnMissingBean
    public RecoveryStrategy tapOnChannelExpansionStrategy() {
        return new TapOnChannelExpansionStrategy();
    }
}

