package me.jasoncampos.ninja.ext.redis;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Singleton
public class JedisInitializer {
	private static final Logger logger = LoggerFactory.getLogger(JedisInitializer.class);
	private final JedisPoolProvider poolProvider;

	@Inject
	public JedisInitializer(final JedisPoolProvider poolProvider) {
		this.poolProvider = poolProvider;
	}

	@Start(order = 20)
	public void onStart() {
		logger.info("Initializing Redis connection pool");
		// Ensure connection is successful
		final JedisPool pool = poolProvider.get();
		try (Jedis jedis = pool.getResource()) {
			logger.info("Testing Redis connection...");
			if (!jedis.isConnected()) {
				throw new RuntimeException("Unable to open connection to redis.");
			}
			logger.info("Successfully connected to Redis");
		}
	}

	@Dispose(order = 20)
	public void onShutdown() {
		logger.info("Closing Redis connection pool");
		final JedisPool pool = poolProvider.get();

		if (!pool.isClosed()) {
			pool.destroy();
		}

		logger.info("Redis connection pool destroyed");
	}
}
