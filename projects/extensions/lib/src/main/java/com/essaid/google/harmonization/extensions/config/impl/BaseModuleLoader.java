package com.essaid.google.harmonization.extensions.config.impl;

import com.essaid.google.harmonization.extensions.config.Module;
import com.essaid.google.harmonization.extensions.config.Module.ModuleType;
import com.essaid.google.harmonization.extensions.config.ModuleLoader;
import com.google.cloud.verticals.foundations.dataharmonization.Transpiler;
import com.google.cloud.verticals.foundations.dataharmonization.data.Container;
import com.google.cloud.verticals.foundations.dataharmonization.debug.proto.Debug.FileInfo;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.PackageContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import com.google.cloud.verticals.foundations.dataharmonization.function.whistle.WhistleFunction;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.FunctionDefinition;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public abstract class BaseModuleLoader implements ModuleLoader {


  protected abstract boolean moduleExists(String modulePath, RuntimeContext context);

  protected abstract ModuleType getModuleType(String modulePath, RuntimeContext context);

  protected String getModulePath(String modulePath, RuntimeContext context) {
    return modulePath.replace('\\', '/');
  }

  protected abstract URL getFullUrl(String modulePath, RuntimeContext context);

  protected abstract URL getBaseUrl(String modulePath, RuntimeContext context);

  protected abstract InputStream getInputStream(String modulePath, RuntimeContext context)
      ;

  @Override
  public List<Module> loadModules(String modulePath, RuntimeContext context) {
    if (!moduleExists(modulePath, context)) {
      return null;
    }
    ModuleType moduleType = getModuleType(modulePath, context);
    URL fullUrl = getFullUrl(modulePath, context);
    URL baseUrl = getBaseUrl(modulePath, context);

    FileInfo fileInfo = FileInfo.newBuilder().setUrl(fullUrl.toString())
        .build();
    Transpiler transpiler = new Transpiler();

    byte[] byteArray = new byte[0];
    try (InputStream is = getInputStream(modulePath, context)) {
      byteArray = ByteStreams.toByteArray(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String whistle = new String(byteArray, StandardCharsets.UTF_8);
    PipelineConfig pipelineConfig = transpiler.transpile(whistle, fileInfo);
    Optional<FunctionDefinition> configFunction = pipelineConfig.getFunctionsList().stream()
        .filter(fd -> fd.getName().equals(Module.CONFIG_FN_NAME) && fd.getArgsCount() == 0)
        .findFirst();
    Container config = context.getDataTypeImplementation().emptyContainer();
    if (configFunction.isPresent()) {
      FunctionDefinition functionDefinition = configFunction.get();
      PackageContext packageContext =
          new PackageContext(ImmutableSet.of(pipelineConfig.getPackageName()),
              pipelineConfig.getPackageName(), null);
      WhistleFunction fn = new WhistleFunction(functionDefinition,
          pipelineConfig, packageContext);
      config = (Container) fn.call(context);
    }
    Module module = new DefaultModule(
        moduleType,
        modulePath,
        fullUrl,
        baseUrl,
        pipelineConfig,
        config
    );
    return List.of(module);
  }

//  protected Module createModule(ModuleType moduleType, String modulePath, URL fullUrl, URL baseUrl,
//      InputStream inputStream,
//      RuntimeContext context) throws IOException {
////    FileInfo fileInfo = FileInfo.newBuilder().setUrl(fullUrl.toString()).build();
//    Transpiler transpiler = new Transpiler();
//
//    byte[] byteArray = ByteStreams.toByteArray(inputStream);
//    String whistle = new String(byteArray, StandardCharsets.UTF_8);
//    PipelineConfig pipelineConfig = transpiler.transpile(whistle, fileInfo);
//    Optional<FunctionDefinition> configFunction = pipelineConfig.getFunctionsList().stream()
//        .filter(fd -> fd.getName().equals(Module.CONFIG_FN_NAME) && fd.getArgsCount() == 0)
//        .findFirst();
//    Container config = context.getDataTypeImplementation().emptyContainer();
//    if (configFunction.isPresent()) {
//      FunctionDefinition functionDefinition = configFunction.get();
//      PackageContext packageContext =
//          new PackageContext(ImmutableSet.of(pipelineConfig.getPackageName()),
//              pipelineConfig.getPackageName(), null);
//      WhistleFunction fn = new WhistleFunction(functionDefinition,
//          pipelineConfig, packageContext);
//      config = (Container) fn.call(context);
//    }
//    Module module = new DefaultModule(
//        moduleType,
//        modulePath,
//        fullUrl,
//        baseUrl,
//        pipelineConfig,
//        config
//    );
//    return module;
//  }
}
