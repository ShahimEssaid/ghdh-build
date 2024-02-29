package com.essaid.google.harmonization.extensions.config.impl;


import com.essaid.google.harmonization.extensions.config.Module;
import com.google.cloud.verticals.foundations.dataharmonization.data.Container;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import java.net.URL;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@RequiredArgsConstructor
@Getter
@Setter
public class DefaultModule  implements Module {

  private final ModuleType type;
  private final String importString;
//  private final ImportPath importPath;
  private final URL url;
  private final URL baseUrl;
  private final PipelineConfig originalPipelineConfig;
  private final Container configuration;

  @Override
  public void setValue(String key, Object value) {

  }

  @Override
  public <T> T getValue(String key) {
    return null;
  }
}
