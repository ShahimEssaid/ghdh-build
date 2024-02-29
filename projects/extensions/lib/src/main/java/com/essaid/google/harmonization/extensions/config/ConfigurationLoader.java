package com.essaid.google.harmonization.extensions.config;

import com.essaid.google.harmonization.extensions.config.impl.DefaultModule;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import java.util.List;

public interface ConfigurationLoader {


  List<DefaultModule> loadWhistleModules(String modulePath);


  PipelineConfig createConfiguration();

  List<DefaultModule> loadModules();

}
