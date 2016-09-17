package me.jasoncampos.ninja.ext.module;

import com.google.inject.AbstractModule;

import me.jasoncampos.ninja.ext.consul.ConsulRegistrator;

/**
 * Runs consul registration on application startup.
 *
 * @author Jason Campos <jcampos8782@gmail.com>
 * @see {@link ConsulRegistrator}
 */
public class ConsulRegistratorModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ConsulRegistrator.class).asEagerSingleton();
	}
}
