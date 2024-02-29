package com.essaid.google.harmonization.extensions.config;

import com.essaid.google.harmonization.extensions.config.impl.DefaultModule;
import com.essaid.google.harmonization.extensions.runtime.impl.DefaultFactory;
import com.google.cloud.verticals.foundations.dataharmonization.Transpiler;
import com.google.cloud.verticals.foundations.dataharmonization.data.Container;
import com.google.cloud.verticals.foundations.dataharmonization.data.DataTypeImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.data.impl.DefaultDataTypeImplementation;
import com.google.cloud.verticals.foundations.dataharmonization.debug.proto.Debug.FileInfo;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.PackageContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.InitializationContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.whistle.WhistleFunction;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportPath;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.FunctionDefinition;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

public class ConfigUtils {

  private static InitializationContext initializationContext;
  private static DataTypeImplementation dataTypeImplementation;

  public static List<DefaultModule> loadWhistleClasspathModules(String importString,
      ClassLoader classLoader) {
    List<DefaultModule> modules = new ArrayList<>();
    try {
      Enumeration<URL> resources = classLoader.getResources(importString);
      while (resources.hasMoreElements()) {
        URL url = resources.nextElement();
        modules.add(loadModule(importString, url));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return modules;
  }

  public static List<DefaultModule> loadWhistlePathsModules(String importString,
      List<Path> paths) {
    List<DefaultModule> modules = new ArrayList<>();

    Path importPath = FileSystems.getDefault().getPath(importString);
    paths.stream().map(p -> {
      try {
        return p.toRealPath().resolve(importPath);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }).filter(Files::exists).forEach(p -> {
          try {
            modules.add(loadModule(importString, p.toUri().toURL()));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
    );
    return modules;
  }

  private static DefaultModule loadModule(String importString, URL moduleUrl)
      throws IOException {

    String urlString = moduleUrl.toString();
    URL baseUrl = new URL(urlString.substring(0, urlString.length() - importString.length()));
    String basePathString = null;

    if (baseUrl.getProtocol().equals("jar")) {
      JarURLConnection jarURLConnection = (JarURLConnection) baseUrl.openConnection();
      basePathString = jarURLConnection.getJarFileURL().getPath();
    } else if (baseUrl.getProtocol().equals("file")) {
      basePathString = baseUrl.getPath();
    } else {
      throw new IllegalStateException("Unknown protocol for url:" + baseUrl.toString());
    }

    ImportPath importPath = ImportPath.of(moduleUrl.getProtocol(),
        FileSystems.getDefault().getPath(importString),
        FileSystems.getDefault().getPath(basePathString));
    FileInfo fileInfo = FileInfo.newBuilder().setUrl(moduleUrl.toString()).build();

    try (InputStream is = moduleUrl.openStream()) {
      byte[] bytes = ByteStreams.toByteArray(is);
      Transpiler transpiler = new Transpiler();
      PipelineConfig pipelineConfig = transpiler.transpile(
          new String(bytes, StandardCharsets.UTF_8), fileInfo);
      Optional<FunctionDefinition> configFunction = pipelineConfig.getFunctionsList().stream()
          .filter(fd -> fd.getName().equals(Module.CONFIG_FN_NAME) && fd.getArgsCount() == 0)
          .findFirst();

      Container container = getDataTypeImplementation().emptyContainer();
      if (configFunction.isPresent()) {
        FunctionDefinition functionDefinition = configFunction.get();
        PackageContext packageContext =
            new PackageContext(ImmutableSet.of(pipelineConfig.getPackageName()),
                pipelineConfig.getPackageName(), importPath);
        WhistleFunction fn = new WhistleFunction(functionDefinition,
            pipelineConfig, packageContext);
        container = (Container) fn.call(getInitializationContext());
      }

      DefaultModule module = new DefaultModule(
          null,
          importString,
          moduleUrl,
          baseUrl,
          pipelineConfig,
          container
      );
      return module;
    }
  }

  private static InitializationContext getInitializationContext() {
    if (initializationContext == null) {
      DefaultFactory factory = new DefaultFactory();
      initializationContext = factory.initializationContextWithBuiltinsUpstream();
    }
    return initializationContext;
  }

  private static DataTypeImplementation getDataTypeImplementation() {
    if (dataTypeImplementation == null) {
      dataTypeImplementation = new DefaultDataTypeImplementation();
    }
    return dataTypeImplementation;
  }

}
