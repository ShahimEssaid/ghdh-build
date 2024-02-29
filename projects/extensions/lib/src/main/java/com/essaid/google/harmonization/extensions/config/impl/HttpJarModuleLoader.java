package com.essaid.google.harmonization.extensions.config.impl;

import com.essaid.google.harmonization.extensions.config.Module.ModuleType;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import java.io.InputStream;
import java.net.URL;

public class HttpJarModuleLoader extends BaseModuleLoader{

  @Override
  protected boolean moduleExists(String modulePath, RuntimeContext context) {
    return false;
  }

  @Override
  protected ModuleType getModuleType(String modulePath, RuntimeContext context) {
    return null;
  }

  @Override
  protected URL getFullUrl(String modulePath, RuntimeContext context) {
    return null;
  }

  @Override
  protected URL getBaseUrl(String modulePath, RuntimeContext context) {
    return null;
  }

  @Override
  protected InputStream getInputStream(String modulePath, RuntimeContext context) {
    return null;
  }
}
