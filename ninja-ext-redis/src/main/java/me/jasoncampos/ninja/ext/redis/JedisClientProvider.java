package me.jasoncampos.ninja.ext.redis;

import javax.inject.Inject;
import javax.inject.Provider;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Provides Jedis connections from an open {@link JedisPool}. Connections obtained from this provider must be manually
 * closed. Since {@link Jedis} implements {@code Closeable}, its recommended that connections are obtained as part of am
 * auto-closeable block. Example:
 *
 * <pre>
 * try (Jedis jedis = provider.get()) {
 * 	// do something
 * }
 * </pre>
 * 
 * @author Jason Campos <jcampos8782@gmail.com>
 */
public class JedisClientProvider implements Provider<Jedis> {

	private final JedisPool pool;

	@Inject
	public JedisClientProvider(final JedisPool pool) {
		this.pool = pool;
	}

	@Override
	public Jedis get() {
		return pool.getResource();
	}

}
