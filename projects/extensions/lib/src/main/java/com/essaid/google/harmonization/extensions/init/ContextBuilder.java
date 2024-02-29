package com.essaid.google.harmonization.extensions.init;

import com.essaid.google.harmonization.extensions.runtime.Factory;
import com.google.cloud.verticals.foundations.dataharmonization.builtins.Builtins;
import com.google.cloud.verticals.foundations.dataharmonization.builtins.BuiltinsConfig;
import com.google.cloud.verticals.foundations.dataharmonization.function.CallableFunction;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.MetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.PackageContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.Registries;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContextImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.InitializationContext;
import com.google.cloud.verticals.foundations.dataharmonization.imports.Loader;
import com.google.cloud.verticals.foundations.dataharmonization.imports.Parser;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.FileLoader;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.WhistleParser;
import com.google.cloud.verticals.foundations.dataharmonization.modifier.arg.ArgModifier;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Option;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Plugin;
import com.google.cloud.verticals.foundations.dataharmonization.registry.PackageRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.Registry;
import com.google.cloud.verticals.foundations.dataharmonization.target.Target.Constructor;
import java.util.Map;

public class ContextBuilder {

  private final Factory factory;
  public MetaData metadata;
  private PackageRegistry<CallableFunction> functionPackageRegistry;
  private Map<String, PackageRegistry<CallableFunction>> pluginFunctionRegistries;
  private PackageRegistry<Constructor> targetPackageRegistry;
  private Registry<ArgModifier> argModifierRegistry;
  private Registry<Loader> loaderRegistry;
  private Registry<Parser> parserRegistry;
  private Registry<Option> optionRegistry;
  private Registries registries;
  private RuntimeContextImplementation contextFactory;

  public ContextBuilder(Factory factory) {
    this.factory = factory;
  }

  public MetaData getMetadata(boolean create) {
    if (metadata == null && create) {
      metadata = factory.metaData();
    }
    return metadata;
  }

  public ContextBuilder setMetadata(MetaData metadata) {
    checkSetter(this.metadata, metadata, "setting metadata.");
    this.metadata = metadata;
    return this;
  }

  public PackageRegistry<CallableFunction> getFunctionPackageRegistry(boolean create) {
    if (functionPackageRegistry == null && create) {
      functionPackageRegistry = factory.functionPackageRegistry();
    }
    return functionPackageRegistry;
  }

  public ContextBuilder setFunctionPackageRegistry(PackageRegistry<CallableFunction> registry) {
    checkSetter(functionPackageRegistry, registry, "setting function package registry.");
    this.functionPackageRegistry = registry;
    return this;
  }

  public Map<String, PackageRegistry<CallableFunction>> getPluginFunctionRegistries(
      boolean create) {
    if (pluginFunctionRegistries == null && create) {
      pluginFunctionRegistries = factory.pluginFunctionRegistries();
    }
    return pluginFunctionRegistries;
  }

  public ContextBuilder setlPluginFunctionRegistries(
      Map<String, PackageRegistry<CallableFunction>> registries) {
    checkSetter(pluginFunctionRegistries, registries, "setting plugin function registries"
        + ".");
    this.pluginFunctionRegistries = registries;
    return this;
  }

  public PackageRegistry<Constructor> getTargetPackageRegistry(boolean create) {
    if (targetPackageRegistry == null && create) {
      targetPackageRegistry = factory.targetPackageRegistry();
    }
    return targetPackageRegistry;
  }

  public ContextBuilder setTargetPackageRegistry(PackageRegistry<Constructor> registry) {
    checkSetter(targetPackageRegistry, registry, "setting target package registry.");
    targetPackageRegistry = registry;
    return this;
  }

  public Registry<ArgModifier> getArgModifierRegistry(boolean create) {
    if (argModifierRegistry == null && create) {
      argModifierRegistry = factory.argModifierRegistry();
    }
    return argModifierRegistry;
  }

  public ContextBuilder setArgModifierRegistry(Registry<ArgModifier> registry) {
    checkSetter(argModifierRegistry, registry, "setting arg modifier registry");
    argModifierRegistry = registry;
    return this;
  }


  public Registry<Loader> getLoaderRegistry(boolean create) {
    if (loaderRegistry == null && create) {
      loaderRegistry = factory.loaderRegistry();
    }
    return loaderRegistry;
  }

  public ContextBuilder setLoaderRegistry(Registry<Loader> registry) {
    checkSetter(loaderRegistry, registry, "setting loader registry");
    loaderRegistry = registry;
    return this;
  }

  public Registry<Parser> getParserRegistry(boolean create) {
    if (parserRegistry == null && create) {
      parserRegistry = factory.parserRegistry();
    }
    return parserRegistry;
  }

  public ContextBuilder setParserRegistry(Registry<Parser> registry) {
    checkSetter(parserRegistry, registry, "setting parser registry");
    parserRegistry = registry;
    return this;
  }


  public Registry<Option> getOptionRegistry(boolean create) {
    if (optionRegistry == null && create) {
      optionRegistry = factory.optionRegistry();
    }
    return optionRegistry;
  }

  public ContextBuilder setOptionRegistry(Registry<Option> registry) {
    checkSetter(optionRegistry, registry, "setting parser registry");
    optionRegistry = registry;
    return this;
  }


  public Registries getRegistries(boolean create) {
    if (registries == null && create) {
      registries = factory.registriesUpstream(
          getFunctionPackageRegistry(create)
          , getPluginFunctionRegistries(create),
          getTargetPackageRegistry(create),
          getArgModifierRegistry(create),
          getLoaderRegistry(create),
          getParserRegistry(create),
          getOptionRegistry(create));
    }
    return registries;
  }

  public ContextBuilder setRegistries(Registries registries) {
    checkSetter(this.registries, registries, "setting registries.");
    this.registries = registries;
    return this;
  }

  public RuntimeContextImplementation getContextFactory(boolean create) {
    if (contextFactory == null && create) {
      contextFactory = factory.runtimeContextImplementation(factory.cancellationToken());
    }
    return contextFactory;
  }

  public ContextBuilder setContextFactory(RuntimeContextImplementation contextFactory) {
    checkSetter(this.contextFactory, contextFactory, "setting context factory.");
    this.contextFactory = contextFactory;
    return this;
  }

  /*
   ************************
   *
   */

  public ContextBuilder register(Loader loader) {
    getRegistries(true).getLoaderRegistry().register(loader);
    return this;
  }

  public ContextBuilder register(Parser parser) {
    getRegistries(true).getParserRegistry().register(parser);
    return this;
  }

  public ContextBuilder register(Plugin plugin) {
    Plugin.load(plugin, getRegistries(true), getMetadata(true));
    return this;
  }

  public ContextBuilder registerBuiltinsPlugin() {
    BuiltinsConfig config = BuiltinsConfig.builder().setAllowFsFuncs(true).build();
    return register(new Builtins(config));
  }

  public ContextBuilder registerFileLoader() {
    return register(new FileLoader());
  }

  public ContextBuilder registerWhistleParser() {
    return register(new WhistleParser());

  }

  public InitializationContext createContext(PackageContext packageContext, MetaData metadata) {
    InitializationContext context =
        (InitializationContext) getContextFactory(true).constructInitialContext(
            packageContext,
            getRegistries(true),
            null,
            metadata
        );
    return context;
  }



  /*
   ********************************
   * privates
   *********************************/

  private void checkSetter(Object currentObject, Object newObject, String message) {
    if (currentObject != null) {
      throw new IllegalArgumentException(("Setter already set with %s while setting with %s, "
          + "and message: %s").formatted(currentObject, newObject, message));
    }

    if (newObject == null) {
      throw new IllegalStateException(
          "Setter called with null argument, and message: %s".formatted(message));
    }
  }
}
