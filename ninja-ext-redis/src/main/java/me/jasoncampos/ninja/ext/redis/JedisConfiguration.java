package me.jasoncampos.ninja.ext.redis;

public interface JedisConfiguration {
	String getHost();

	int getPort();

	String getPassword();

	int getTimeout();

	boolean useSsl();

	int getMaxPoolSize();
}
