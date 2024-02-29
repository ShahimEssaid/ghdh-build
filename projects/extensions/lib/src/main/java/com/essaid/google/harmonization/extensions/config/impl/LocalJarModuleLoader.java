package com.essaid.google.harmonization.extensions.config.impl;

import com.essaid.google.harmonization.extensions.config.Module.ModuleType;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LocalJarModuleLoader extends BaseModuleLoader {

  private final Path jarPath;
  private final FileSystem fileSystem;

  public LocalJarModuleLoader(Path jarPath, FileSystem fileSystem) {
    Objects.requireNonNull(jarPath);
    Objects.requireNonNull(fileSystem);

    if (!Files.exists(jarPath)) {
      throw new IllegalArgumentException(String.format("Jar path %s for %s does not exist",
          jarPath, getClass().getName()));
    }

    if (!jarPath.toString().toLowerCase().endsWith(".jar")) {
      throw new IllegalArgumentException(
          String.format("Jar file %s does not end with .jar. Wrong path?", jarPath.toString()));
    }

    this.jarPath = jarPath;
    this.fileSystem = fileSystem;
  }


  @Override
  protected boolean moduleExists(String modulePath, RuntimeContext context) {
    return Files.exists(jarPath.resolve(fileSystem.getPath(modulePath)));
  }

  @Override
  protected ModuleType getModuleType(String modulePath, RuntimeContext context) {
    return ModuleType.LOCAL_JAR;
  }


  @Override
  protected URL getFullUrl(String modulePath, RuntimeContext context) {
    try {
      return jarPath.resolve(fileSystem.getPath(modulePath)).toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected URL getBaseUrl(String modulePath, RuntimeContext context) {
    try {
      return jarPath.toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected InputStream getInputStream(String modulePath, RuntimeContext context) {
    try {
      return Files.newInputStream(jarPath.resolve(fileSystem.getPath(modulePath)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
