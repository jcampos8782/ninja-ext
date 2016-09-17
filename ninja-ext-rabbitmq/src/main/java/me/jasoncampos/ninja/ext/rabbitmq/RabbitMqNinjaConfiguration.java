package me.jasoncampos.ninja.ext.rabbitmq;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.rabbitmq.client.Address;

import me.jasoncampos.certified.rabbitmq.model.RabbitMqConfiguration;
import ninja.utils.NinjaProperties;

public class RabbitMqNinjaConfiguration implements RabbitMqConfiguration {

	private final NinjaProperties ninjaProperties;

	@Inject
	public RabbitMqNinjaConfiguration(final NinjaProperties ninjaProperties) {
		this.ninjaProperties = ninjaProperties;
	}

	@Override
	public String getUsername() {
		return ninjaProperties.get("rabbitmq.username");
	}

	@Override
	public String getPassword() {
		return ninjaProperties.get("rabbitmq.password");
	}

	@Override
	public List<Address> getHosts() {
		final String hosts = ninjaProperties.get("rabbitmq.hosts");

		if (hosts == null) {
			return Collections.emptyList();
		}

		// Comma-separated list of host:port
		return Arrays.stream(hosts.split(","))
				.map(a -> a.split(":"))
				.map(a -> new Address(a[0], Integer.parseInt(a[1])))
				.collect(Collectors.toList());
	}

	@Override
	public String getVirtualHost() {
		return ninjaProperties.get("rabbitmq.virtual_host");
	}
}
