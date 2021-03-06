/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.rest.client.dsl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.cloud.dataflow.rest.client.DataFlowOperations;
import org.springframework.cloud.dataflow.rest.resource.StreamDefinitionResource;
import org.springframework.util.Assert;

/**
 * Represents a Stream deployed on DataFlow server. Instances of this class are created using a fluent style builder
 * pattern.  For for instance:
 * <pre>
 *     {@code
 *     Stream stream = Stream.builder(dataflowOperations).definition("time | log").create().deploy();
 *     }
 * </pre>
 *
 * A fluent style that separates source, processor and sink parts can also be used via
 * <pre>
 *     {@code
 *     Stream stream = Stream.builder(dataflowOperations).source("time").sink("log").create().deploy();
 *     }
 * </pre>
 * @author Vinicius Carvalho
 *
 */
public class Stream {

	private String name;

	private List<StreamApplication> applications = new LinkedList<>();

	private String definition;

	private DataFlowOperations client;

	Stream(String name, List<StreamApplication> applications, String definition,
			DataFlowOperations client) {
		this.name = name;
		this.applications = applications;
		this.definition = definition;
		this.client = client;
	}

	/**
	 * Fluent API method to create a {@link StreamBuilder}.
	 * @param client {@link DataFlowOperations} client instance
	 * @return A fluent style builder to create streams
	 */
	public static StreamBuilder builder(DataFlowOperations client) {
		return new StreamBuilder(client);
	}

	String getDefinition() {
		return this.definition;
	}

	/**
	 * Undeploy the current {@link Stream}. This method invokes the remote server
	 * @return A reference the the {@link StreamDefinition} so one can invoke other builder operations such as {@link StreamDefinition#deploy()}
	 */
	public StreamDefinition undeploy() {
		client.streamOperations().undeploy(this.name);
		return new StreamDefinition(this.name, this.client, this.definition,
				this.applications);
	}

	/**
	 * Destroy the stream from the server. This method invokes the remote server
	 */
	public void destroy() {
		client.streamOperations().destroy(this.name);
	}

	/**
	 * @return Status of the deployed stream
	 */
	public String getStatus() {
		StreamDefinitionResource resource = client.streamOperations()
				.getStreamDefinition(this.name);
		return resource.getStatus();
	}

	public static class StreamNameBuilder {

		private String name;

		private List<StreamApplication> applications = new LinkedList<>();

		private DataFlowOperations client;

		private String definition;

		StreamNameBuilder(String name, DataFlowOperations client) {
			this.client = client;
			Assert.hasLength(name, "Stream name can't be empty");
			this.name = name;
		}

		/**
		 * Appends a {@link StreamApplication} as a source for this stream
		 * @param source - The {@link StreamApplication} being added
		 * @return a {@link SourceBuilder} to continue the building of the Stream
		 */
		public SourceBuilder source(StreamApplication source) {
			Assert.notNull(source, "Source application can't be null");
			return new SourceBuilder(
					source.type(StreamApplication.ApplicationType.SOURCE), this);
		}

		/**
		 * Creates a Stream bypassing the fluent API and just using the provided
		 * definition
		 * @param definiton the Stream definition to use
		 * @return A {@link Stream} object
		 */
		public StreamDefinitionBuilder definition(String definiton) {
			Assert.hasLength(name, "Stream definition can't be empty");
			this.definition = definiton;
			return new StreamDefinitionBuilder(this.name, this.client, this.definition);
		}

		/**
		 * Creates the Stream. This method will invoke the remote server and create a stream
		 * @return StreamDefinition to allow deploying operations on the created Stream
		 */
		private StreamDefinition create() {
			return new StreamDefinition(this.name, this.client, this.definition,
					this.applications);
		}

		private void addApplication(StreamApplication application) {
			if (contains(application)) {
				throw new IllegalStateException(
						"There's already an application with the same definition in this stream");
			}
			this.applications.add(application);
		}

		private boolean contains(StreamApplication application) {
			for (StreamApplication app : this.applications) {
				if (app.getType().equals(application.getType())
						&& app.getIdentity().equals(application.getIdentity())) {
					return true;
				}
			}
			return false;
		}
	}

	public static class StreamDefinitionBuilder {

		private String name;

		private DataFlowOperations client;

		private String definition;

		private StreamDefinitionBuilder(String name, DataFlowOperations client,
				String definition) {
			this.name = name;
			this.client = client;
			this.definition = definition;
		}

		/**
		 * Creates the Stream. This method will invoke the remote server and create a stream
		 * @return StreamDefinition to allow deploying operations on the created Stream
		 */
		public StreamDefinition create() {
			return new StreamDefinition(this.name, this.client, this.definition,
					Collections.emptyList());
		}
	}

	public static class SourceBuilder extends BaseBuilder {

		private SourceBuilder(StreamApplication source, StreamNameBuilder parent) {
			super(source, parent);
		}

		/**
		 * Appends a {@link StreamApplication} as a processor for this stream
		 * @param processor - The {@link StreamApplication} being added
		 * @return a {@link ProcessorBuilder} to continue the building of the Stream
		 */
		public ProcessorBuilder processor(StreamApplication processor) {
			Assert.notNull(processor, "Processor application can't be null");
			return new ProcessorBuilder(
					processor.type(StreamApplication.ApplicationType.PROCESSOR),
					this.parent);
		}

		/**
		 * Appends a {@link StreamApplication} as a sink for this stream
		 * @param sink - The {@link StreamApplication} being added
		 * @return a {@link SinkBuilder} to continue the building of the Stream
		 */
		public SinkBuilder sink(StreamApplication sink) {
			Assert.notNull(sink, "Sink application can't be null");
			return new SinkBuilder(sink.type(StreamApplication.ApplicationType.SINK),
					this.parent);
		}
	}

	public static class ProcessorBuilder extends BaseBuilder {

		private ProcessorBuilder(StreamApplication application,
				StreamNameBuilder parent) {
			super(application, parent);
		}

		/**
		 * Appends a {@link StreamApplication} as a processor for this stream
		 * @param processor - The {@link StreamApplication} being added
		 * @return a {@link ProcessorBuilder} to continue the building of the Stream
		 */
		public ProcessorBuilder processor(StreamApplication processor) {
			Assert.notNull(processor, "Processor application can't be null");
			return new ProcessorBuilder(
					processor.type(StreamApplication.ApplicationType.PROCESSOR),
					this.parent);
		}
		/**
		 * Appends a {@link StreamApplication} as a sink for this stream
		 * @param sink - The {@link StreamApplication} being added
		 * @return a {@link SinkBuilder} to continue the building of the Stream
		 */
		public SinkBuilder sink(StreamApplication sink) {
			Assert.notNull(sink, "Sink application can't be null");
			return new SinkBuilder(sink.type(StreamApplication.ApplicationType.SINK),
					this.parent);
		}

	}

	public static class SinkBuilder extends BaseBuilder {

		private SinkBuilder(StreamApplication application, StreamNameBuilder parent) {
			super(application, parent);
		}

		public StreamDefinition create() {
			return this.parent.create();
		}

	}

	static abstract class BaseBuilder {

		protected StreamApplication application;

		protected StreamNameBuilder parent;

		public BaseBuilder(StreamApplication application, StreamNameBuilder parent) {
			this.application = application;
			this.parent = parent;
			this.parent.addApplication(application);
		}

	}

}
