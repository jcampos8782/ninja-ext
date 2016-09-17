package me.jasoncampos.ninja.ext.rabbitmq;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Connection;

import me.jasoncampos.certified.rabbitmq.RabbitMqConnectionProvider;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;

@Singleton
public class RabbitMqInitializer {
	private static final Logger logger = LoggerFactory.getLogger(RabbitMqInitializer.class);
	private final RabbitMqConnectionProvider connectionProvider;

	@Inject
	public RabbitMqInitializer(final RabbitMqConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	@Start(order = 20)
	public void onStart() {
		logger.info("Initializing RabbitMq");
		// Ensure connection is successful
		final Connection connection = connectionProvider.get();
		logger.info(String.format("Connected to RabbitMq. host=%s", connection.getAddress().getHostAddress()));
	}

	@Dispose(order = 20)
	public void onShutdown() {
		logger.info("Closing RabbitMq connection");
		final Connection connection = connectionProvider.get();

		try {
			if (connection.isOpen()) {
				connection.close();
			}
		} catch (final IOException e) {
			//
		}

		logger.info("RabbitMq connection closed");
	}
}
