package com.essaid.google.harmonization.extensions;

import com.google.cloud.verticals.foundations.dataharmonization.function.context.MetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.Registries;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Plugin;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class ClasspathAndPathsPlugin implements Plugin {

  public final String NAME = "ClasspathAndPaths";

  public ClasspathAndPathsPlugin(List<String> initialImportPaths, List<String> importRoots,
      ClassLoader importClassloader, boolean classloaderFirst){

    /*
    import roots are either a local directory, a local jar, a local zip file, or a http/https
    url of those. Examples:

    file://

     */
  }

  @Override
  public String getPackageName() {
    return NAME;
  }

  @Override
  public void onLoaded(Registries registries, MetaData metaData) {
    Plugin.super.onLoaded(registries, metaData);
  }
}
