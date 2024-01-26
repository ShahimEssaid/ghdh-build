package example;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.cloud.verticals.foundations.dataharmonization.builtins.Builtins;
import com.google.cloud.verticals.foundations.dataharmonization.builtins.BuiltinsConfig;
import com.google.cloud.verticals.foundations.dataharmonization.data.Data;
import com.google.cloud.verticals.foundations.dataharmonization.data.serialization.impl.JsonSerializerDeserializer;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.MetaData;
import com.google.cloud.verticals.foundations.dataharmonization.function.context.impl.DefaultMetaData;
import com.google.cloud.verticals.foundations.dataharmonization.imports.ImportPath;
import com.google.cloud.verticals.foundations.dataharmonization.imports.impl.FileLoader;
import com.google.cloud.verticals.foundations.dataharmonization.init.Engine;
import com.google.cloud.verticals.foundations.dataharmonization.init.Engine.Builder;
import com.google.cloud.verticals.foundations.dataharmonization.init.Engine.InitializedBuilder;
import com.google.cloud.verticals.foundations.dataharmonization.init.initializer.ExternalConfigExtractor;
import com.google.cloud.verticals.foundations.dataharmonization.plugin.Plugin;
import com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

  static String mappingFile = "files/example-1/main.wstl";
  static String inputFile = "files/example-1/in.json";

  public static void main(String[] args) throws Exception {
//    example_1();
    programmatic();
  }

  static void example_1() throws Exception {
    com.google.cloud.verticals.foundations.dataharmonization.Main.main(
        new String[]{
            "-m",
            mappingFile,
            "-i",
            inputFile}
    );
  }

  /**
   * The general idea is to create a
   * {@link com.google.cloud.verticals.foundations.dataharmonization.proto.Pipeline.PipelineConfig}
   */
  static void programmatic() throws IOException {

    // We need an Engine instance to run a transformation.
    // We use a Builder, then an InitializedBuilder, then get the engine.
    // To use the main constructor: Engine.Builder.Builder(ConfigExtractorBase, List<Plugin>)
    // We need a configuration extractor and a list of plugins that the Engine will use.
    // The code below shows how to construct these manually but you can see the cli Main or
    // the Engine.java file to see other shortcut options for doing this with more defaults.

    // We first need an ImportPath that represents the main entry point file and a root directory
    // to use for resolving root-relative imports (not absolute or self relative).  The cli's Main
    // class uses the file given on the -m option as the entry file and assumes the parent folder
    // is the root folder but that doesn't have to be the case.
    Path mainFilePath = FileSystems.getDefault().getPath(mappingFile);
    Path importRoot = mainFilePath.getParent();
    ImportPath mainImportPath = ImportPath.of(FileLoader.NAME, mainFilePath, importRoot);
    // we now have a reference to the "source" information for the mapping. In this case it is a
    // whistle file with .wstl extension but the source can be other formats.

    //
    ExternalConfigExtractor mainConfigExtractor = ExternalConfigExtractor.of(mainImportPath);
    List<Plugin> plugins = new ArrayList<>();

    // the builtins plugin
    BuiltinsConfig.Builder builtinsBuilder = BuiltinsConfig.builder();
    // customize how builtins work. Here, disable filesystem access.
    builtinsBuilder.setAllowFsFuncs(false);
    BuiltinsConfig builtinsConfig = builtinsBuilder.build();
    Builtins builtins = new Builtins(builtinsConfig);
    plugins.add(builtins);

    Engine.Builder initializedBuilderBuilder = new Builder(mainConfigExtractor, plugins);
    initializedBuilderBuilder.withDefaultPlugins();

    MetaData metaData = new DefaultMetaData();
    InitializedBuilder initializedBuilder = initializedBuilderBuilder.initialize(metaData);


    Engine engine = initializedBuilder.build();

    File file = new File(inputFile);
    FileInputStream fis = new FileInputStream(file);
    byte[] json = ByteStreams.toByteArray(fis);
    Data data = new JsonSerializerDeserializer().deserialize(json);

    Data transformedData = engine.transform(data);

    byte[] transformedJson = new JsonSerializerDeserializer().serialize(transformedData);
    Gson prettyPrinter = new GsonBuilder().setPrettyPrinting().create();
    String outpuot = prettyPrinter.toJson(prettyPrinter.fromJson(new String(transformedJson, UTF_8), JsonElement.class));

    System.out.println(outpuot);
  }


  private static PipelineConfig getWhistlePipelineConfig() {
    PipelineConfig pipelineConfig = null;

    return pipelineConfig;
  }

}
