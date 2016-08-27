# ninja-extensions
Guice modules and other extensions to the Ninja Framework (http://www.ninjaframework.org).

## Hibernate Module
A Guice `Module` to enable programmatic configuration of Hibernate. See http://www.github.com/jcampos8782/guice-persist-hibernate). 

Respects Ninja frameworks `@UnitOfWork` and GuicePersist's `@Transactional` annotations. 

Exposed bindings:
1. `Provider<Session>`
1. `Provider<UnitOfWork>`
1. `Provider<EntityManager>`

### Usage
Install just as you would any other `Module`. Intended as a replacement (not a supplment) to Ninja Framework's `JpaModule`. 

```java
public void configure(NinjaProperties ninjaProperties) {
    install(new HibernateModule.Builder().entityProvider(SomeEntityClassProvider.class).propertyProvider(SomePropertyProvider.class).build());
}
```