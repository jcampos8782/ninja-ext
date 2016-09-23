package me.jasoncampos.ninja.ext.redis;

import javax.inject.Inject;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import ninja.utils.NinjaProperties;
import redis.clients.jedis.Protocol;

public class JedisNinjaConfiguration implements JedisConfiguration {

	private final NinjaProperties ninjaProperties;

	@Inject
	public JedisNinjaConfiguration(final NinjaProperties ninjaProperties) {
		this.ninjaProperties = ninjaProperties;
	}

	@Override
	public String getHost() {
		return ninjaProperties.getWithDefault("redis.host", Protocol.DEFAULT_HOST);
	}

	@Override
	public int getPort() {
		return ninjaProperties.getIntegerWithDefault("redis.port", Protocol.DEFAULT_PORT);
	}

	public int getDatabase() {
		return ninjaProperties.getIntegerWithDefault("redis.database", Protocol.DEFAULT_DATABASE);
	}

	@Override
	public int getTimeout() {
		return ninjaProperties.getIntegerWithDefault("redis.timeout", Protocol.DEFAULT_TIMEOUT);
	}

	@Override
	public String getPassword() {
		return ninjaProperties.get("redis.password");
	}

	@Override
	public boolean useSsl() {
		return ninjaProperties.getBooleanWithDefault("redis.use_ssl", false);
	}

	@Override
	public int getMaxPoolSize() {
		return ninjaProperties.getIntegerWithDefault("redis.max_connections", GenericObjectPoolConfig.DEFAULT_MAX_TOTAL);
	}
}
