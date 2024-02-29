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

public class LocalFileModuleLoader extends BaseModuleLoader {

  private final Path directoryPath;
  private final FileSystem fileSystem;

  public LocalFileModuleLoader(Path directoryPath, FileSystem fileSystem) {
    Objects.requireNonNull(directoryPath);
    Objects.requireNonNull(fileSystem);

    if (!Files.exists(directoryPath)) {
      throw new IllegalArgumentException(
          String.format("Directory path %s for %s does not exist", directoryPath,
              getClass().getName()));
    }
    this.directoryPath = directoryPath;
    this.fileSystem = fileSystem;
  }

  @Override
  protected boolean moduleExists(String modulePath, RuntimeContext context) {
    return Files.exists(directoryPath.resolve(fileSystem.getPath(modulePath)));
  }

  @Override
  protected ModuleType getModuleType(String modulePath, RuntimeContext context) {
    return ModuleType.LOCAL_FILE;
  }

  @Override
  protected URL getBaseUrl(String modulePath, RuntimeContext context) {
    try {
      return directoryPath.toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected URL getFullUrl(String modulePath, RuntimeContext context) {
    try {
      return directoryPath.resolve(fileSystem.getPath(modulePath)).toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected InputStream getInputStream(String modulePath, RuntimeContext context) {
    try {
      return Files.newInputStream(directoryPath.resolve(fileSystem.getPath(modulePath)));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

//  @Override
//  public Module loadModule(String modulePath, RuntimeContext context) {
//    Path relativePath = fileSystem.getPath(modulePath);
//    Path fullPath = directoryPath.resolve(relativePath);
//    if (!Files.exists(fullPath)) {
//      return null;
//    }
//    Module module = null;
//    try (InputStream is = Files.newInputStream(fullPath)) {
//      module = createModule(
//          ModuleType.LOCAL_FILE, modulePath, fullPath.toUri().toURL(),
//          directoryPath.toUri().toURL(),
//          is, context);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//    return module;
//  }
}
