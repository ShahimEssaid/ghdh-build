package com.essaid.google.harmonization.extensions.config.impl;

import com.essaid.google.harmonization.extensions.config.Module.ModuleType;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpFileModuleLoader extends BaseModuleLoader {

  private final URL baseUrl;

  public HttpFileModuleLoader(String baseUrl) {
    baseUrl = baseUrl.replaceFirst("/*$", "/");
    try {
      this.baseUrl = new URL(baseUrl);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected boolean moduleExists(String modulePath, RuntimeContext context) {
    HttpURLConnection connection = getConnection(modulePath, context);
    try {
      int responseCode = connection.getResponseCode();
      if (responseCode >= 400) {
        return false;
      } else if (responseCode == 200) {
        return true;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return false;
  }

  @Override
  protected ModuleType getModuleType(String modulePath, RuntimeContext context) {
    return ModuleType.HTTP_FILE;
  }

  @Override
  protected URL getFullUrl(String modulePath, RuntimeContext context) {
    try {
      URL requestUrl = new URL(baseUrl, modulePath);
      return requestUrl;
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected URL getBaseUrl(String modulePath, RuntimeContext context) {
    return baseUrl;
  }

  @Override
  protected InputStream getInputStream(String modulePath, RuntimeContext context) {
    HttpURLConnection connection = getConnection(modulePath, context);
    try {
      return connection.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private HttpURLConnection getConnection(String modulePath, RuntimeContext context) {
    try {
      HttpURLConnection urlConnection = (HttpURLConnection) getFullUrl(modulePath,
          context).openConnection();
      urlConnection.setRequestMethod("GET");
      return urlConnection;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
}
