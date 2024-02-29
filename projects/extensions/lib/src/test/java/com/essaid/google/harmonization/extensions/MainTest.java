package com.essaid.google.harmonization.extensions;

import com.essaid.google.harmonization.extensions.runtime.Utils;
import com.google.cloud.verticals.foundations.dataharmonization.builtins.Builtins;
import com.google.cloud.verticals.foundations.dataharmonization.data.Data;
import com.google.cloud.verticals.foundations.dataharmonization.function.CallableFunction;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.MetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.PackageContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.Registries;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContextImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.StackFrame;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultMetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultRegistries;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultRuntimeContext.DefaultImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultStackFrame.DefaultBuilder;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.InitializationContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.whistle.WhistleFunction;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportPath;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportProcessor;
import com.google.cloud.verticals.foundations.dataharmonization.imports.Loader;
import com.google.cloud.verticals.foundations.dataharmonization.imports.Parser;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.DefaultImportProcessor;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.FileLoader;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.WhistleParser;
import com.google.cloud.verticals.foundations.dataharmonization.init.Engine;
import com.google.cloud.verticals.foundations.dataharmonization.init.initializer.ExternalConfigExtractor;
import com.google.cloud.verticals.foundations.dataharmonization.modifier.arg.ArgModifier;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Option;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Plugin;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import com.google.cloud.verticals.foundations.dataharmonization.registry.PackageRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.Registry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.impl.DefaultPackageRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.registry.impl.DefaultRegistry;
import com.google.cloud.verticals.foundations.dataharmonization.target.Target.Constructor;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class MainTest {

  static String main_test_mappingFile = "files/main-test/main.wstl";


  static String main_test_inputFile = "files/main-test/in.json";

  static String mains_main_test_mappingFile = "files/main-test/mains/main.wstl";
  static String import_root = "files/main-test/";

  @Test
  void cliExample() throws Exception {
    com.google.cloud.verticals.foundations.dataharmonization.Main.main(
        new String[]{
            "-m",
            main_test_mappingFile,
            "-i",
            main_test_inputFile}
    );
  }

  @Test
  void mainExample() throws IOException {

    MetaData metadata = new DefaultMetaData();

    // Registries
    PackageRegistry<CallableFunction> functionPackageRegistry = new DefaultPackageRegistry<>();
    Map<String, PackageRegistry<CallableFunction>> pluginFunctionRegistries = new HashMap<>();
    PackageRegistry<Constructor> targetPackageRegistry = new DefaultPackageRegistry<>();
    Registry<ArgModifier> argModifierRegistry = new DefaultRegistry<>();
    Registry<Loader> loaderRegistry = new DefaultRegistry<>();
    Registry<Parser> parserRegistry = new DefaultRegistry<>();
    Registry<Option> optionRegistry = new DefaultRegistry<>();

    Registries registries = new DefaultRegistries(functionPackageRegistry,
        pluginFunctionRegistries,
        targetPackageRegistry,
        argModifierRegistry,
        loaderRegistry,
        parserRegistry,
        optionRegistry
    );

    registries.getLoaderRegistry().register(new FileLoader());
    registries.getParserRegistry().register(new WhistleParser());

    Builtins builtins = new Builtins();
    Plugin.load(builtins, registries, metadata);

    ImportPath importPath = ImportPath.of("file", Path.of(mains_main_test_mappingFile),
        Path.of(import_root));

    ExternalConfigExtractor externalConfigExtractor = ExternalConfigExtractor.of(importPath);

    ImportProcessor importProcessor = new DefaultImportProcessor();

    PipelineConfig pipelineConfig = externalConfigExtractor.initialize(registries, metadata,
        importProcessor);

    Set<String> globalAliasedPackages = ImmutableSet.of(pipelineConfig.getPackageName());
    PackageContext packageContext = new PackageContext(globalAliasedPackages,
        pipelineConfig.getPackageName(),
        externalConfigExtractor.getImportPath());

    CallableFunction mainFunction = new WhistleFunction(pipelineConfig.getRootBlock(),
        pipelineConfig,
        packageContext
    );

    RuntimeContextImplementation mainRtxImpl = new DefaultImplementation();
    StackFrame.Builder rootFrameBuilder = new DefaultBuilder();

    RuntimeContext context = new InitializationContext(packageContext, registries, importProcessor,
        mainRtxImpl, rootFrameBuilder, metadata);

    Engine engine = Utils.newEngine(context, mainFunction);

    Data inData = context.getDataTypeImplementation().primitiveOf("root value");

    Data output = engine.transform(inData);
    Data output2 = engine.transform(inData);

    engine.close();

    System.out.println(output);
    System.out.println(output2);

  }

}
