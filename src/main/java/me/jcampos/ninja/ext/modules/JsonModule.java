package me.jcampos.ninja.ext.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.multibindings.OptionalBinder;

import ninja.bodyparser.BodyParserEngineJson;
import ninja.template.TemplateEngineJson;

/**
 * A practical JSON module.
 * <ul>
 * <li>Java 8 time support</li>
 * <li>Timestamps/Dates/Instants are written in ISO8601 format</li>
 * <li>No support for JSONP</li>
 * </ul>
 *
 * @author Jason Campos <jcmapos8782@gmail.com>
 */
public class JsonModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TemplateEngineJson.class);
		bind(BodyParserEngineJson.class);

		OptionalBinder.newOptionalBinder(binder(), ObjectMapper.class)
				.setBinding()
				.toProvider(new Provider<ObjectMapper>() {
					@Override
					public ObjectMapper get() {
						final ObjectMapper objectMapper = new ObjectMapper();
						objectMapper.registerModule(new JavaTimeModule());
						objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
						return objectMapper;
					}
				})
				.in(Singleton.class);
	}
}
