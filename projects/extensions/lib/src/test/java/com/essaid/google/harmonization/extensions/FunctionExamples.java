package com.essaid.google.harmonization.extensions;

import com.essaid.google.harmonization.extensions.config.Module;
import com.essaid.google.harmonization.extensions.init.ContextBuilder;
import com.essaid.google.harmonization.extensions.runtime.Factory;
import com.essaid.google.harmonization.extensions.runtime.impl.DefaultFactory;
import com.google.cloud.verticals.foundations.dataharmonization.Transpiler;
import com.google.cloud.verticals.foundations.dataharmonization.data.Container;
import com.google.cloud.verticals.foundations.dataharmonization.data.Data;
import com.google.cloud.verticals.foundations.dataharmonization.debug.proto.Debug.FileInfo;
import com.google.cloud.verticals.foundations.dataharmonization.debug.proto.Debug.FileInfo.Builder;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.PackageContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultMetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.InitializationContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.whistle.WhistleFunction;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportPath;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.FunctionDefinition;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class FunctionExamples {

  public static String EXAMPLES = "function_examples.wstl";
  private ImportPath importPath;
  private Factory factory = new DefaultFactory();
  private ContextBuilder contextBuilder = new ContextBuilder(factory);

  @Test
  void executeConfigFunction() throws IOException {
    PipelineConfig pipelineConfig = loadExamples();
    Optional<FunctionDefinition> configFunction = pipelineConfig.getFunctionsList().stream()
        .filter(fd -> fd.getName().equals(Module.CONFIG_FN_NAME) && fd.getArgsCount() == 0)
        .findFirst();

    Container container = null;
    FunctionDefinition functionDefinition = configFunction.get();
    PackageContext packageContext =
        new PackageContext(ImmutableSet.of(pipelineConfig.getPackageName()),
            pipelineConfig.getPackageName(), importPath);
    WhistleFunction fn = new WhistleFunction(functionDefinition,
        pipelineConfig, packageContext);

    InitializationContext initializationContext = null;

//    initializationContext = factory.initializationContextWithBuiltinsUpstream();
    initializationContext =
        contextBuilder
            .registerBuiltinsPlugin()
            .registerFileLoader()
            .registerWhistleParser()
            .createContext(fn.getLocalPackageContext(null), new DefaultMetaData());

    Data call = fn.call(initializationContext);
    System.out.println(call);
  }


  PipelineConfig loadExamples() throws IOException {
    URL url = getClass().getClassLoader().getResource(EXAMPLES);
    String urlString = url.toString();
    URL baseUrl = new URL(urlString.substring(0, urlString.length() - EXAMPLES.length()));

    String basePath = null;

    if (baseUrl.getProtocol().equals("jar")) {
      JarURLConnection jarURLConnection = (JarURLConnection) baseUrl.openConnection();
      basePath = jarURLConnection.getJarFileURL().getPath();

    } else if (baseUrl.getProtocol().equals("file")) {
      basePath = baseUrl.getPath();

    } else {
      throw new IllegalStateException("Unknown protocol for url:" + baseUrl.toString());
    }

    importPath = ImportPath.of(url.getProtocol(), Path.of(EXAMPLES),
        Path.of(basePath));

    byte[] bytes = ByteStreams.toByteArray(url.openStream());
    Builder fileInfo = FileInfo.newBuilder();
    fileInfo.setUrl(url.toString());
    Transpiler transpiler = new Transpiler();
    PipelineConfig pipelineConfig = transpiler.transpile(
        new String(bytes, StandardCharsets.UTF_8), fileInfo.build());

    return pipelineConfig;
  }

}
