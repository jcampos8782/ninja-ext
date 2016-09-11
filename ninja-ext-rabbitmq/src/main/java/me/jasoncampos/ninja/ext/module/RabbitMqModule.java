package me.jasoncampos.ninja.ext.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;

import me.jasoncampos.certified.rabbitmq.model.RabbitMqConfiguration;
import me.jasoncampos.certified.rabbitmq.serializer.PayloadSerializer;
import me.jasoncampos.certified.rabbitmq.serializer.StringPayloadSerializer;
import me.jasoncampos.ninja.ext.rabbitmq.RabbitMqInitializer;
import me.jasoncampos.ninja.ext.rabbitmq.RabbitMqNinjaConfiguration;

public class RabbitMqModule extends AbstractModule {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMqModule.class);

	@Override
	protected void configure() {
		logger.info("Intializing {}", this.getClass().getName());

		// Allow for configuration override (by consul for example)
		OptionalBinder.newOptionalBinder(binder(), RabbitMqConfiguration.class)
				.setDefault()
				.to(RabbitMqNinjaConfiguration.class);

		// Default to the string serializer
		OptionalBinder.newOptionalBinder(binder(), PayloadSerializer.class)
				.setDefault()
				.to(StringPayloadSerializer.class);

		install(new me.jasoncampos.certified.rabbitmq.RabbitMqModule());

		// Eagerly bootstrap connection
		bind(RabbitMqInitializer.class).asEagerSingleton();
	}
}
