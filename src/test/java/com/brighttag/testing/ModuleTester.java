package com.brighttag.testing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletScopes;
import com.google.inject.util.Types;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * A test utility class that can be used to Guice modules. Construct the class with a
 * {@link Module} and then call {@link #dependsOn} zero or more times. Finally call
 * {@link #exposes(Key[])} and {@link #exposesNothingElse} which performs actual validation.
 * For example:
 * <pre>
 * new ModuleTester(new MyModule())
 *     .dependsOn(Key.get(String.class, Names.named("my.service.property")), "yes")
 *     .dependsOn(Key.get(MyService.class), MyServiceImpl.class)
 *     .dependsOn(Key.get(MyDependency.class), new MyDependencyProvider())
 *     .exposes(MyThing.class)
 *     .exposesNothingElse()
 *     .verify();
 * </pre>
 *
 * This also supports testing {@link RequestScoped} bindings via {@link #exposesRequestScoped(Key[])}.
 * For example:
 * <pre>
 * new ModuleTester(new MyModule())
 *     .exposesRequestScoped(Key.get(MyRequest.class))
 *     .verify();
 * </pre>
 *
 * This also supports testing {@link MultibinderBinding multibindings} via
 * {@link #exposesMultibinding(Class...)}. For example:
 * <pre>
 * new ModuleTester(new MyModule())
 *     .exposesMultibinding(Service.class)
 *     .verify();
 * </pre>
 *
 * If {@link #exposesNothingElse} is too strict for you, this also supports validating only
 * certain keys are not exposed via {@link #hides(Key...)}. For example:
 * <pre>
 * new ModuleTester(new MyModule())
 *     .exposesMultibinding(Service.class)
 *     .hides(MyServiceImpl.class)
 *     .verify();
 * </pre>
 *
 * @author mkemp
 * @author codyaray
 * @since 9/14/12
 */
public class ModuleTester {

  private final Iterable<Module> modules;
  private final DependencyModule dependencies;
  private final Set<Key<?>> exposed;
  private final Set<Key<?>> requestScoped;
  private final Set<Key<?>> hidden;
  private final Set<Type> multibindings;
  private boolean exposesNothingElse;

  public ModuleTester(Module... modules) {
    this.modules = Arrays.asList(modules);
    this.dependencies = new DependencyModule();
    this.exposed = Sets.newHashSet();
    this.requestScoped = Sets.newHashSet();
    this.hidden = Sets.newHashSet();
    this.multibindings = Sets.newHashSet();
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, Class<? extends T> thingClass) {
    return dependsOn(Key.get(keyClass), thingClass);
  }

  public <T> ModuleTester dependsOn(Key<T> key, Class<? extends T> thingClass) {
    dependencies.keyToClass(key, thingClass);
    return this;
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, Class<? extends T> thingClass,
      Class<? extends Annotation> scope) {
    return dependsOn(Key.get(keyClass), thingClass, scope);
  }

  public <T> ModuleTester dependsOn(Key<T> key, Class<? extends T> thingClass,
      Class<? extends Annotation> scope) {
    dependencies.keyToClass(key, thingClass, scope);
    return this;
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, TypeLiteral<? extends T> thingType) {
    return dependsOn(Key.get(keyClass), thingType);
  }

  public <T> ModuleTester dependsOn(Key<T> key, TypeLiteral<? extends T> thingType) {
    dependencies.keyToTypeLiteral(key, thingType);
    return this;
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, TypeLiteral<? extends T> thingType,
      Class<? extends Annotation> scope) {
    return dependsOn(Key.get(keyClass), thingType, scope);
  }

  public <T> ModuleTester dependsOn(Key<T> key, TypeLiteral<? extends T> thingType,
      Class<? extends Annotation> scope) {
    dependencies.keyToTypeLiteral(key, thingType, scope);
    return this;
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, Provider<? extends T> thingProvider) {
    return dependsOn(Key.get(keyClass), thingProvider);
  }

  public <T> ModuleTester dependsOn(Key<T> key, Provider<? extends T> thingProvider) {
    dependencies.keyToProvider(key, thingProvider);
    return this;
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, Provider<? extends T> thingProvider,
      Class<? extends Annotation> scope) {
    return dependsOn(Key.get(keyClass), thingProvider, scope);
  }

  public <T> ModuleTester dependsOn(Key<T> key, Provider<? extends T> thingProvider,
      Class<? extends Annotation> scope) {
    dependencies.keyToProvider(key, thingProvider, scope);
    return this;
  }

  public <T> ModuleTester dependsOn(Class<T> keyClass, T thing) {
    return dependsOn(Key.get(keyClass), thing);
  }

  public <T> ModuleTester dependsOn(Key<T> key, T thing) {
    dependencies.keyToInstance(key, thing);
    return this;
  }

  public ModuleTester exposes(Class<?>... classes) {
    for (Class<?> c : classes) {
      exposed.add(Key.get(c));
    }
    return this;
  }

  public ModuleTester exposes(Key<?>... keys) {
    exposed.addAll(Arrays.asList(keys));
    return this;
  }

  public ModuleTester exposesRequestScoped(Class<?>... classes) {
    for (Class<?> c : classes) {
      requestScoped.add(Key.get(c));
    }
    return this;
  }

  public ModuleTester exposesRequestScoped(Key<?>... keys) {
    requestScoped.addAll(Arrays.asList(keys));
    return this;
  }

  public ModuleTester exposesMultibinding(Class<?>... classes) {
    multibindings.addAll(Arrays.asList(classes));
    for (Class<?> c : classes) {
      exposes(Key.get(Types.newParameterizedType(Set.class, c)));
    }
    return this;
  }

  public ModuleTester hides(Class<?>... classes) {
    for (Class<?> c : classes) {
      hidden.add(Key.get(c));
    }
    return this;
  }

  public ModuleTester hides(Key<?>... keys) {
    hidden.addAll(Arrays.asList(keys));
    return this;
  }

  public ModuleTester exposesNothingElse() {
    exposesNothingElse = true;
    return this;
  }

  public void verify() throws Exception {
    Injector injector = Guice.createInjector(
        Iterables.concat(modules, Arrays.asList(dependencies)));
    for (Key<?> key : exposed) {
      assertNotNull("Binding for " + key + " should be exposed", injector.getBinding(key));
    }
    for (Key<?> key : hidden) {
      assertNull("Binding for " + key + " should not be exposed", injector.getBinding(key));
    }
    final Set<Provider<?>> providers = Sets.newHashSet();
    for (Key<?> key : requestScoped) {
      Provider<?> provider = injector.getProvider(key);
      assertNotNull(provider);
      providers.add(provider);
    }
    doInRequestScope(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        for (Provider<?> provider : providers) {
          assertNotNull(provider.get());
        }
        return null;
      }
    });
    if (exposesNothingElse) {
      // Just filter out a few things that seem to be always exposed by Guice
      Set<Key<?>> allExposedKeys = Sets.difference(injector.getAllBindings().keySet(),
          ImmutableSet.of(Key.get(Injector.class), Key.get(Stage.class), Key.get(Logger.class)));
      for (Key<?> key : allExposedKeys) {
        if (!isMultibinding(key)) {
          assertTrue(key + " should not be exposed", exposed.contains(key) || dependencies.getKeys().contains(key));
        }
      }
    }
  }

  private static final String MULTIBINDING_ELEMENT = "interface com.google.inject.multibindings.Element";

  private boolean isMultibinding(Key<?> key) {
    return (multibindings.contains(key.getTypeLiteral().getType()) &&
            key.getAnnotationType() != null &&
            key.getAnnotationType().toString().equals(MULTIBINDING_ELEMENT));  // XXX gross hack
  }

  private static Object doInRequestScope(Callable<Object> callable) throws Exception {
    return ServletScopes.scopeRequest(callable, Collections.<Key<?>, Object>emptyMap()).call();
  }

  /**
   * Handles the actual set of dependent bindings.
   */
  private static class DependencyModule extends AbstractModule {

    private final Set<Binding> bindings = Sets.newHashSet();
    private final Set<Key<?>> keys = Sets.newHashSet();

    @Override
    protected void configure() {
      bindScope(RequestScoped.class, ServletScopes.REQUEST);
      for (Binding binding : bindings) {
        binding.bind();
      }
    }

    public <T> void keyToClass(final Key<T> key, final Class<? extends T> thingClass) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).to(thingClass);
        }
      });
    }

    public <T> void keyToClass(final Key<T> key, final Class<? extends T> thingClass,
        final Class<? extends Annotation> scope) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).to(thingClass).in(scope);
        }
      });
    }

    public <T> void keyToTypeLiteral(final Key<T> key, final TypeLiteral<? extends T> thingType) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).to(thingType);
        }
      });
    }

    public <T> void keyToTypeLiteral(final Key<T> key, final TypeLiteral<? extends T> thingType,
        final Class<? extends Annotation> scope) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).to(thingType).in(scope);
        }
      });
    }

    public <T> void keyToProvider(final Key<T> key, final Provider<? extends T> thingProvider) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).toProvider(thingProvider);
        }
      });
    }

    public <T> void keyToProvider(final Key<T> key, final Provider<? extends T> thingProvider,
        final Class<? extends Annotation> scope) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).toProvider(thingProvider).in(scope);
        }
      });
    }

    public <T> void keyToInstance(final Key<T> key, final T thing) {
      bindings.add(new Binding() {
        @Override
        public void bind() {
          addKey(key);
          DependencyModule.this.bind(key).toInstance(thing);
        }
      });
    }

    public Set<Key<?>> getKeys() {
      return keys;
    }

    private void addKey(Key<?> key) {
      keys.add(key);
    }

    private interface Binding {
      void bind();
    }
  }

}
