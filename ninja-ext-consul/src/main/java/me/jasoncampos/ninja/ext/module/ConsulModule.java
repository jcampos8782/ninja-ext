package me.jasoncampos.ninja.ext.module;

import javax.inject.Inject;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.CatalogClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.EventClient;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.PreparedQueryClient;
import com.orbitz.consul.SessionClient;
import com.orbitz.consul.StatusClient;

import ninja.utils.NinjaProperties;

/**
 * Bootstraps a Consul client from {@link NinjaProperties}. Required properties:
 * <ul>
 * <li>consul.url - FQDN and port of the consul host (eg: http://localhost:8500)</li>
 * </ul>
 *
 * @author Jason Campos <jcampos8782@gmail.com>
 */
public class ConsulModule extends AbstractModule {

	private final NinjaProperties ninjaProperties;

	public ConsulModule(final NinjaProperties ninjaProperties) {
		this.ninjaProperties = ninjaProperties;
	}

	@Override
	protected void configure() {
		Preconditions.checkNotNull(ninjaProperties.get("consul.url"));
		final Consul consul = Consul.builder().withUrl(ninjaProperties.get("consul.url")).build();
		bind(Consul.class).toInstance(consul);
	}

	@Inject
	@Provides
	public AgentClient getAgentClient(final Consul consul) {
		return consul.agentClient();
	}

	@Inject
	@Provides
	public CatalogClient getCatalogClient(final Consul consul) {
		return consul.catalogClient();
	}

	@Inject
	@Provides
	public EventClient getEventClient(final Consul consul) {
		return consul.eventClient();
	}

	@Inject
	@Provides
	public HealthClient getHealthClient(final Consul consul) {
		return consul.healthClient();
	}

	@Inject
	@Provides
	public PreparedQueryClient getPreparedQueryClient(final Consul consul) {
		return consul.preparedQueryClient();
	}

	@Inject
	@Provides
	SessionClient getSessionClient(final Consul consul) {
		return consul.sessionClient();
	}

	@Inject
	@Provides
	StatusClient getStatusClient(final Consul consul) {
		return consul.statusClient();
	}
}
