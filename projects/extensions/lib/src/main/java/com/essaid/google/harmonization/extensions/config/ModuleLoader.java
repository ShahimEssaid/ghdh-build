package com.essaid.google.harmonization.extensions.config;

import com.google.cloud.verticals.foundations.dataharmonization.function.context.RuntimeContext;
import java.util.List;

public interface ModuleLoader {

  List<Module> loadModules(String modulePath, RuntimeContext context);

}
