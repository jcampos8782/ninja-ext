package me.jasoncampos.ninja.ext.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.OptionalBinder;

import me.jasoncampos.ninja.ext.redis.JedisConfiguration;
import me.jasoncampos.ninja.ext.redis.JedisInitializer;
import me.jasoncampos.ninja.ext.redis.JedisNinjaConfiguration;

public class RedisModule extends AbstractModule {
	private static final Logger logger = LoggerFactory.getLogger(RedisModule.class);

	@Override
	protected void configure() {
		logger.info("Intializing {}", this.getClass().getName());

		// Allow for configuration override (by consul for example)
		OptionalBinder.newOptionalBinder(binder(), JedisConfiguration.class)
				.setDefault()
				.to(JedisNinjaConfiguration.class);

		// Eagerly bootstrap connection
		bind(JedisInitializer.class).asEagerSingleton();
	}
}
