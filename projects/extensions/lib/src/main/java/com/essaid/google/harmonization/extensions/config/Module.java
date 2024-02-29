package com.essaid.google.harmonization.extensions.config;

public interface Module {

  String CONFIG_FN_NAME = "_ex_config";
  String CONFIG_MODULE_ID = "module_id";
  String CONFIG_OVERRIDE_PRIORITY = "module_priority";

  ModuleType getType();

  void setValue(String key, Object value);

  <T> T getValue(String key);

  enum ModuleType {
    LOCAL_FILE("file://"), LOCAL_JAR("jar:file://"), LOCAL_ZIP(""), HTTP_FILE(""), HTTP_JAR(""),
    HTTP_ZIP("");

    private final String prefix;

    private ModuleType(String prefix) {
      this.prefix = prefix;
    }


    public static ModuleType getModuleType(String url) {

      return null;
    }
  }

}
