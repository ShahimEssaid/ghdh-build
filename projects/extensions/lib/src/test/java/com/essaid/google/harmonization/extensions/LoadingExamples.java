package com.essaid.google.harmonization.extensions;

import com.essaid.google.harmonization.extensions.config.ConfigUtils;
import com.essaid.google.harmonization.extensions.config.impl.DefaultModule;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class LoadingExamples {


  @Test
  void loadClassPath(){
    ClassLoader classLoader = getClass().getClassLoader();
    String modulePathString = "function_examples.wstl";

    List<DefaultModule> classpathModules = ConfigUtils.loadWhistleClasspathModules(modulePathString, classLoader);

    List<Path> loadingPaths = new ArrayList<>();
    loadingPaths.add(FileSystems.getDefault().getPath("files", "function_examples_1"));
    loadingPaths.add(FileSystems.getDefault().getPath("files", "function_examples_2"));

    List<DefaultModule> pathsModules = ConfigUtils.loadWhistlePathsModules(modulePathString, loadingPaths);
    pathsModules.get(0).getOriginalPipelineConfig().getRootBlockOrBuilder();

    System.out.println(classpathModules);
  }

}
