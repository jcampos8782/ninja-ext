package me.jasoncampos.ninja.ext.redis;

import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;

@Singleton
public class JedisPoolProvider implements Provider<JedisPool> {
	private static final Logger logger = LoggerFactory.getLogger(JedisPoolProvider.class);
	private final JedisConfiguration configuration;
	private final ReentrantLock lock = new ReentrantLock();

	private volatile JedisPool pool;

	public JedisPoolProvider(final JedisConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public JedisPool get() {
		if (pool != null) {
			return pool;
		}

		logger.debug("Waiting for connection lock...");
		lock.lock();
		logger.debug("Lock obtained.");

		try {
			if (pool != null) {
				logger.debug("Pool initialized while awaiting lock");
			} else {

				if (logger.isDebugEnabled()) {
					logger.debug(
							String.format("host=%s, port=%d, timeout=%d, password=%s, useSsl=%s, maxPoolSize=%d",
									configuration.getHost(),
									configuration.getPort(),
									configuration.getTimeout(),
									configuration.getPassword() != null ? configuration.getPassword().replaceAll(".", "*") : "",
									configuration.useSsl(),
									configuration.getMaxPoolSize()));
				}

				final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
				poolConfig.setMaxTotal(configuration.getMaxPoolSize());

				pool = new JedisPool(
						poolConfig,
						configuration.getHost(),
						configuration.getPort(),
						configuration.getTimeout(),
						configuration.getPassword(),
						configuration.useSsl());
			}
		} finally {
			lock.unlock();
			logger.debug("Lock released");
		}

		return pool;
	}
}
