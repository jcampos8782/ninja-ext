package me.jcampos.ninja.ext.modules;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;

import me.jasoncampos.inject.persist.hibernate.HibernateEntityClassProvider;
import me.jasoncampos.inject.persist.hibernate.HibernatePersistModule;
import me.jasoncampos.inject.persist.hibernate.HibernatePropertyProvider;
import ninja.jpa.JpaInitializer;
import ninja.jpa.UnitOfWork;
import ninja.jpa.UnitOfWorkInterceptor;

/**
 * Provides hibernate bootstrapping for the <a href="http://www.ninjaframework.org">Ninja Framework</a>. Uses a
 * Hibernate-specific GuicePersist module for bootstrapping (see
 * <a href="https://github.com/jcampos8782/guice-persist-hibernatet">guice-persist-hibernate</a>). <br/>
 * <br/>
 * Key differences between this module and {@link ninja.jpa.JpaModule JpaModule}:
 *
 * <ul>
 * <li>Enablement of programmatic bootstrapping of hibernate via the
 * {@link me.jasoncampos.inject.persist.hibernate.HibernateEntityClassProvider HibernateEntityClassProvider} and
 * {@link me.jasoncampos.inject.persist.hibernate.HibernateEntityClassProvider HibernatePropertyProvider}</li>
 * <li>Inject {@code Provider<Session>} may be used in lieu of {@code Provider<EntityManager>} (though not
 * required)</li>
 * </ul>
 * Similarities:
 * <ul>
 * <li>{@link ninja.jpa.UnitOfWorkInterceptor @UnitOfWork} works without modification</li>
 * <li>{@code Provider<EntityManager>} works without modification</li>
 * <li>All GuicePersist functionality is intact ({@code @Transactional} for example)</li>
 * <li>Bootstrapped on startup automagically by ninja framework.</li>
 * </ul>
 *
 * NOTE: You must manually install this module alongside any standard Ninja module (NinjaClassicModule for example) or
 * inside of your FrameworkModule. Simply setting {@code NinjaClassicModule.jpa(true)} will *not* install this module.
 * In fact, installation of that module may conflict. The two should not be installed together.
 *
 * @author Jason Campos <jcampos8782@gmail.com>
 */
public class HibernateModule extends AbstractModule {
	private static final Logger logger = LoggerFactory.getLogger(HibernateModule.class);

	private final Class<? extends HibernateEntityClassProvider> hibernateEntityClassProviderClass;
	private final Class<? extends HibernatePropertyProvider> hibernatePropertyProviderClass;
	private final HibernateEntityClassProvider hibernateEntityClassProvider;
	private final HibernatePropertyProvider hibernatePropertyProvider;

	private HibernateModule(
			final Class<? extends HibernateEntityClassProvider> hibernateEntityClassProviderClass,
			final Class<? extends HibernatePropertyProvider> hibernatePropertyProviderClass,
			final HibernateEntityClassProvider hibernateEntityClassProvider,
			final HibernatePropertyProvider hibernatePropertyProvider) {
		this.hibernateEntityClassProviderClass = hibernateEntityClassProviderClass;
		this.hibernatePropertyProviderClass = hibernatePropertyProviderClass;
		this.hibernateEntityClassProvider = hibernateEntityClassProvider;
		this.hibernatePropertyProvider = hibernatePropertyProvider;
	}

	@Override
	protected void configure() {
		logger.info("Intializing {}", this.getClass().getName());

		// Binding can either be to a class or an instance
		if (hibernateEntityClassProvider != null) {
			bind(HibernateEntityClassProvider.class).toInstance(hibernateEntityClassProvider);
		} else {
			bind(HibernateEntityClassProvider.class).to(hibernateEntityClassProviderClass).in(Singleton.class);
		}

		if (hibernatePropertyProvider != null) {
			bind(HibernatePropertyProvider.class).toInstance(hibernatePropertyProvider);
		} else {
			bind(HibernatePropertyProvider.class).to(hibernatePropertyProviderClass).in(Singleton.class);
		}

		install(new HibernatePersistModule());

		final UnitOfWorkInterceptor unitOfWorkInterceptor = new UnitOfWorkInterceptor();
		requestInjection(unitOfWorkInterceptor);

		// class-level @UnitOfWork
		bindInterceptor(
				annotatedWith(UnitOfWork.class),
				any(),
				unitOfWorkInterceptor);

		// method-level @UnitOfWork
		bindInterceptor(
				any(),
				annotatedWith(UnitOfWork.class),
				unitOfWorkInterceptor);

		bind(JpaInitializer.class).asEagerSingleton();
	}

	public static class Builder {
		private Class<? extends HibernateEntityClassProvider> hibernateEntityClassProviderClass;
		private Class<? extends HibernatePropertyProvider> hibernatePropertyProviderClass;
		private HibernateEntityClassProvider hibernateEntityClassProvider;
		private HibernatePropertyProvider hibernatePropertyProvider;

		public Builder entityClassProvider(final Class<? extends HibernateEntityClassProvider> entityClassProvider) {
			this.hibernateEntityClassProviderClass = entityClassProvider;
			return this;
		}

		public Builder entityClassProvider(final HibernateEntityClassProvider entityClassProvider) {
			this.hibernateEntityClassProvider = entityClassProvider;
			return this;
		}

		public Builder propertyProvider(final Class<? extends HibernatePropertyProvider> propertyProvider) {
			this.hibernatePropertyProviderClass = propertyProvider;
			return this;
		}

		public Builder propertyProvider(final HibernatePropertyProvider propertyProvider) {
			this.hibernatePropertyProvider = propertyProvider;
			return this;
		}

		public HibernateModule build() {
			Preconditions.checkState(
					hibernateEntityClassProvider != null || hibernateEntityClassProviderClass != null,
					"A HibernateEntityClassProvider is required to bootstrap hibernate.");

			Preconditions.checkState(
					hibernatePropertyProvider != null || hibernatePropertyProviderClass != null,
					"A HibernatePropertyProvider is requried to bootstrap hibernate.");

			return new HibernateModule(
					hibernateEntityClassProviderClass,
					hibernatePropertyProviderClass,
					hibernateEntityClassProvider,
					hibernatePropertyProvider);
		}
	}
}
