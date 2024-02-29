package com.essaid.google.harmonization.extensions.runtime;

import com.google.cloud.verticals.foundations.dataharmonization.builtins.Builtins;
import com.google.cloud.verticals.foundations.dataharmonization.builtins.BuiltinsConfig;
import com.google.cloud.verticals.foundations.dataharmonization.function.CallableFunction;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.CancellationToken;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.MetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.PackageContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.Registries;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContextImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.StackFrame;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultCancellationToken;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultMetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultRegistries;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultRuntimeContext.DefaultImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultStackFrame.DefaultBuilder;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.InitializationContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.NoopCancellationToken;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportPath;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportProcessor;
import com.google.cloud.verticals.foundations.dataharmonization.imports.Loader;
import com.google.cloud.verticals.foundations.dataharmonization.imports.Parser;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.DefaultImportProcessor;
import com.google.cloud.verticals.foundations.dataharmonization.modifier.arg.ArgModifier;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Option;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Plugin;
import com.google.cloud.verticals.foundations.dataharmonization.registry.PackageRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.Registry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.impl.DefaultPackageRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.impl.DefaultRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.target.Target.Constructor;
import com.google.common.collect.ImmutableSet;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Factory {

  default ImportPath importPathFinal(String loaderName, Path importedFile, Path importRoot) {
    return ImportPath.of(loaderName, importedFile, importRoot);
  }

  default PackageContext packageContextFinal(Set<String> globalPackages,
      String currentPackage,
      ImportPath importPath) {
    currentPackage = currentPackage != null ? currentPackage : "main";
    globalPackages = globalPackages != null ? globalPackages : ImmutableSet.of(currentPackage);
    importPath = importPath != null ? importPath : importPathFinal(null, null, null);

    PackageContext context = new PackageContext(globalPackages, currentPackage, importPath);
    return context;
  }

  default PackageRegistry<CallableFunction> functionPackageRegistry() {
    return new DefaultPackageRegistry<>();
  }

  default Map<String, PackageRegistry<CallableFunction>> pluginFunctionRegistries() {
    return new HashMap<>();
  }

  default PackageRegistry<Constructor> targetPackageRegistry() {
    return new DefaultPackageRegistry<>();
  }

  default Registry<ArgModifier> argModifierRegistry() {
    return new DefaultRegistry<>();
  }

  default Registry<Loader> loaderRegistry() {
    return new DefaultRegistry<>();
  }

  default Registry<Parser> parserRegistry() {
    return new DefaultRegistry<>();
  }

  default Registry<Option> optionRegistry() {
    return new DefaultRegistry<>();
  }

  default BuiltinsConfig.Builder builtinsConfigBuilderUpstream() {
    return BuiltinsConfig.builder();
  }

  default Builtins builtinsUpstream(BuiltinsConfig builtinsConfig) {
    builtinsConfig = builtinsConfig != null ?
        builtinsConfig : builtinsConfigBuilderUpstream().build();

    return new Builtins(builtinsConfig);
  }

  default Registries registriesUpstream(
      PackageRegistry<CallableFunction> functionPackageRegistry,
      Map<String, PackageRegistry<CallableFunction>> pluginFunctionRegistries,
      PackageRegistry<Constructor> targetPackageRegistry,
      Registry<ArgModifier> argModifierRegistry,
      Registry<Loader> loaderRegistry,
      Registry<Parser> parserRegistry,
      Registry<Option> optionRegistry
  ) {

    functionPackageRegistry = functionPackageRegistry != null ?
        functionPackageRegistry : functionPackageRegistry();
    pluginFunctionRegistries = pluginFunctionRegistries != null ?
        pluginFunctionRegistries : pluginFunctionRegistries();
    targetPackageRegistry = targetPackageRegistry != null ? targetPackageRegistry :
        targetPackageRegistry();
    argModifierRegistry = argModifierRegistry != null ? argModifierRegistry :
        argModifierRegistry();
    loaderRegistry = loaderRegistry != null ? loaderRegistry : loaderRegistry();
    parserRegistry = parserRegistry != null ? parserRegistry : parserRegistry();
    optionRegistry = optionRegistry != null ? optionRegistry : optionRegistry();

    Registries registries = new DefaultRegistries(
        functionPackageRegistry,
        pluginFunctionRegistries,
        targetPackageRegistry,
        argModifierRegistry,
        loaderRegistry,
        parserRegistry,
        optionRegistry);
    return registries;
  }

  default ImportProcessor importProcessorUpstream(Path initialPathAsSeen) {
    return initialPathAsSeen != null ? new DefaultImportProcessor(initialPathAsSeen)
        : new DefaultImportProcessor();
  }

  default CancellationToken cancellationToken() {
    return new DefaultCancellationToken();
  }

  default CancellationToken cancellationTokenNoOpUpstream() {
    return new NoopCancellationToken();
  }

  default RuntimeContextImplementation runtimeContextImplementation(
      CancellationToken cancellationToken
  ) {
    cancellationToken = cancellationToken != null ? cancellationToken : cancellationToken();
    return new DefaultImplementation(cancellationToken);
  }

  default StackFrame.Builder stackFrameBuilderUpstream() {
    return new DefaultBuilder();
  }

  default MetaData metaData() {
    return new DefaultMetaData();
  }

  default InitializationContext initializationContextUpstream(
      PackageContext packageContext,
      Registries registries,
      ImportProcessor importProcessor,
      RuntimeContextImplementation runtimeContextImplementation,
      StackFrame.Builder stackframeBulder,
      MetaData metaData
  ) {

    packageContext =
        packageContext != null ? packageContext : packageContextFinal(null, null, null);
    registries = registries != null ? registries : registriesUpstream(null, null, null, null, null,
        null, null);
    importProcessor = importProcessor != null? importProcessor : importProcessorUpstream(null);
    runtimeContextImplementation = runtimeContextImplementation != null?
        runtimeContextImplementation: runtimeContextImplementation(null);
    stackframeBulder = stackframeBulder != null? stackframeBulder : stackFrameBuilderUpstream();
    metaData = metaData != null? metaData: metaData();

    InitializationContext context = new InitializationContext(
        packageContext,
        registries,
        importProcessor,
        runtimeContextImplementation,
        stackframeBulder,
        metaData
    );
    return context;
  }

  default InitializationContext initializationContextUpstream(){
    return initializationContextUpstream(null, null, null, null, null, null);
  }

  default InitializationContext initializationContextWithBuiltinsUpstream(){
    InitializationContext context = initializationContextUpstream();
    Plugin.load(builtinsUpstream(null), context.getRegistries(),
        context.getMetaData());
    return context;
  }


  /////////////////////////////////



}
