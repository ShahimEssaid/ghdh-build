package com.essaid.google.harmonization.extensions.config.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

public class ModuleLoaderTests {

  @Test
  void loadHttpFile(){
    HttpFileModuleLoader loader = new HttpFileModuleLoader("https://raw.githubusercontent.com/HL7/phenomics-exchange-ig/master/");
    boolean exists = loader.moduleExists("ig.ini", null);
    assertTrue(exists);

    try(InputStream is = loader.getInputStream("ig.ini", null)){
      String s = new String(ByteStreams.toByteArray(is), StandardCharsets.UTF_8);
      System.out.println(s);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
