package com.essaid.google.harmonization.extensions.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleClassLoaders {


  public static final ModuleClassLoaders instance = new ModuleClassLoaders();
  private static final InheritableThreadLocal<ModuleClassLoaders> threadClassLoaders = new InheritableThreadLocal<>();
  private final Map<String, ClassLoader> classLoadersMap = new ConcurrentHashMap<>();

  public static ModuleClassLoaders getClassLoaders() {
    ModuleClassLoaders moduleClassLoaders = threadClassLoaders.get();
    if (moduleClassLoaders == null) {
      moduleClassLoaders = instance;
    }
    return instance;
  }

  public static void setThreadClassLoaders(ModuleClassLoaders moduleClassLoaders){
    threadClassLoaders.set(moduleClassLoaders);
  }

  public static ModuleClassLoaders getThreadClassLoaders(){
    return threadClassLoaders.get();
  }

}
