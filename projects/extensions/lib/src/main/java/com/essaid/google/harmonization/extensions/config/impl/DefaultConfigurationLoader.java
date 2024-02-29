package com.essaid.google.harmonization.extensions.config.impl;

import com.essaid.google.harmonization.extensions.config.ConfigurationLoader;
import com.google.cloud.verticals.foundations.dataharmonization.Transpiler;
import com.google.cloud.verticals.foundations.dataharmonization.debug.proto.Debug.FileInfo;
import com.google.cloud.verticals.foundations.dataharmonization.debug.proto.Debug.FileInfo.Builder;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;


public class DefaultConfigurationLoader implements ConfigurationLoader {

  private final String mainPath;
  private final ClassLoader classLoader;
  private final List<Path> loadingPath;

  public DefaultConfigurationLoader(String mainPath, ClassLoader classLoader,
      List<Path> loadingPath) {
    this.mainPath = mainPath;
    this.classLoader = classLoader;
    this.loadingPath = loadingPath;
  }

  @Override
  public List<DefaultModule> loadWhistleModules(String modulePath) {
    return null;
  }

  @Override
  public PipelineConfig createConfiguration() {
    return null;
  }

  @Override
  public List<DefaultModule> loadModules() {
    List<DefaultModule> modules = new ArrayList<>();
    try {
      classLoader.getResources(mainPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  private void loadPath(String path, List<DefaultModule> modules, Set<String> seenPaths) {

    if (!seenPaths.add(path)) {
      return;
    }

    try {
      Enumeration<URL> urls = classLoader.getResources(path);
      while (urls.hasMoreElements()) {
        URL url = urls.nextElement();
        loadUrl(path, url, modules);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  private void loadUrl(String path, URL url, List<DefaultModule> modules) {
    try (InputStream is = url.openStream()) {
      byte[] bytes = ByteStreams.toByteArray(url.openStream());
      Builder fileInfo = FileInfo.newBuilder();
      fileInfo.setUrl(url.toString());
      Transpiler transpiler = new Transpiler();
      PipelineConfig pipelineConfig = transpiler.transpile("", fileInfo.build());

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
