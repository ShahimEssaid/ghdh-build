package com.essaid.google.harmonization.extensions;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

//@Disabled
public class ApiExamplesTest {

  static String example_1_mappingFile = "files/example-1/main.wstl";
  static String example_1_inputFile = "files/example-1/in.json";


  @Test
  void cliExample_1() throws Exception {

    com.google.cloud.verticals.foundations.dataharmonization.Main.main(
        new String[]{
            "-m",
            example_1_mappingFile,
            "-i",
            example_1_inputFile}
    );
  }


}
