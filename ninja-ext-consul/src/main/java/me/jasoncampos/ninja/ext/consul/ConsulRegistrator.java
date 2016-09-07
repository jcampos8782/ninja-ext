package me.jasoncampos.ninja.ext.consul;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.NotRegisteredException;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

/**
 * Registers the service with consul. The following properties are required to be available in application.conf:
 * <ul>
 * <li>consul.ttl - Heartbeat TTL (in seconds). If a heartbeat is not received by consul within the TTL window, the
 * service is marked as unhealthy. A heartbeat will be sent at {@code consul.ttl / 2} seconds.</li>
 * </ul>
 *
 * @author Jason Campos <jcampos8782@gmail.com>
 */
public class ConsulRegistrator {
	private static final Logger logger = LoggerFactory.getLogger(ConsulRegistrator.class);

	private static final int DEFAULT_TTL = 60;
	private final AgentClient consul;
	private final NinjaProperties ninjaProperties;
	private final String id = "1"; // TODO inject? What is this for?

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> heartbeat;

	@Inject
	public ConsulRegistrator(final AgentClient consul, final NinjaProperties ninjaProperties) {
		this.consul = consul;
		this.ninjaProperties = ninjaProperties;
	}

	@Start(order = 90)
	public void onStart() throws NotRegisteredException {
		final String name = ninjaProperties.get("application.name");
		final int port = ninjaProperties.getInteger("ninja.port");
		final int ttl = ninjaProperties.getIntegerWithDefault("consul.ttl", DEFAULT_TTL);

		logger.info(String.format("Registering %s with consul. id: %s, port: %d, ttl: %d", name, id, port, ttl));
		consul.register(port, ttl, name, id);

		final Runnable beater = new Runnable() {
			@Override
			public void run() {
				try {
					consul.pass(id);
				} catch (final NotRegisteredException e) {
					// Ignore Consul will mark as critical
				}
			}
		};

		heartbeat = scheduler.scheduleAtFixedRate(beater, ttl / 2, ttl / 2, TimeUnit.SECONDS);
	}

	@Dispose(order = 90)
	public void onShutdown() {
		logger.info(String.format("Deregistering %s from consul. id: %s", ninjaProperties.get("application.name"), id));
		heartbeat.cancel(true);
		consul.deregister(id);
	}
}
