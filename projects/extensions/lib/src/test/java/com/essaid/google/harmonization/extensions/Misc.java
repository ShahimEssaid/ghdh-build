package com.essaid.google.harmonization.extensions;

import com.essaid.google.harmonization.extensions.runtime.Utils;
import com.google.cloud.verticals.foundations.dataharmonization.init.Engine;
import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//@Disabled
public class Misc {

  static String example_2_mappingFile = "files/example-2/main.wstl";
  static String example_2_inputFile = "files/example-2/in.json";


  @Test
  void cliExample() throws Exception {

    com.google.cloud.verticals.foundations.dataharmonization.Main.main(
        new String[]{
            "-m",
            example_2_mappingFile,
            "-i",
            example_2_inputFile}
    );
  }

  @Test
  void someTest() {
    System.out.println(Paths.get(".").toAbsolutePath());
  }


  @Test
  void setup() {
    Engine engine = Utils.newEngine(null, null);
    Assertions.assertNotNull(engine);
  }


  @Test
  void resourceOne() throws IOException {
    Enumeration<URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
    while (resources.hasMoreElements()) {
      System.out.println(resources.nextElement());
    }

    ClassLoader classLoader = getClass().getClassLoader();
    if (classLoader instanceof URLClassLoader) {
      URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
      URL[] urls = urlClassLoader.getURLs();
      for (URL url : urls) {
        System.out.println("URL: " + url);
      }

    }

  }

  @Test
  void printJarUrl() throws IOException {

    URL url = new URL("jar:file:/home/essaids/.gradle/caches/modules-2/files-2.1/org"
        + ".hamcrest/hamcrest-core/1.3/42a25dc3219429f0e5d060061f71acb49bf010a0/hamcrest-core-1.3.jar!/META-INF/MANIFEST.MF");

//    URL url = new URL("file:/home/essaids/git/gh/se/ghdh-build/projects/extensions/lib/build"
//        + "/resources/test/META-INF/MANIFEST.MF");

    URLConnection urlConnection = url.openConnection();

    String protocol = url.getProtocol();

    if (urlConnection instanceof JarURLConnection) {
      JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
      System.out.println("getJarFileURL: " + jarURLConnection.getJarFileURL());
      System.out.println("getJarFile: " + jarURLConnection.getJarFile());
      System.out.println("getJarFile: " + jarURLConnection.getJarFileURL());
    }

    String file = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);

    System.out.println(file);

  }


  @Test
  void jarUrls() throws IOException, URISyntaxException {

    URL jarUrl = new URL("jar:file://somefile.jar!/");
    URL fileUrl = new URL("somefile.jar");
    URI fileUri = fileUrl.toURI();
    boolean absolute = fileUri.isAbsolute();

    URI uri = jarUrl.toURI();

    Path cwd = Path.of("");
    URI cwdUri = cwd.toAbsolutePath().toUri();
    uri = cwdUri.resolve(uri);


    fileUrl = cwdUri.resolve(fileUrl.toURI()).toURL();

    String path2 = uri.toURL().getPath();
    String path = jarUrl.getPath();

    URLConnection urlConnection = jarUrl.openConnection();

    if (urlConnection instanceof JarURLConnection) {
      JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
      URL jarFileURL = jarURLConnection.getJarFileURL();
      String path1 = jarFileURL.getPath();
      String file = jarFileURL.getFile();

      System.out.println(jarFileURL);
    }

    System.out.println(path);
  }

  @Test
  void jarUlrs2() throws IOException {

    Iterator<URL> iterator = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF")
        .asIterator();
    URL jarUlr = null;
    for (Iterator<URL> it = iterator; it.hasNext(); ) {
      URL url = it.next();
      if (url.toString().startsWith("jar")){
        jarUlr = url;
        break;
      }
    }

    String path = jarUlr.getPath();
    String file = jarUlr.getFile();

    JarURLConnection jarURLConnection = (JarURLConnection) jarUlr.openConnection();

    URL url = jarURLConnection.getURL();

    JarFile jarFile = jarURLConnection.getJarFile();
    URL jarFileURL = jarURLConnection.getJarFileURL();
    String file1 = jarURLConnection.getJarFileURL().getFile();
    String path1 = jarURLConnection.getJarFileURL().getPath();

    System.out.println("");
  }

  @Test
  void readZipFile() throws IOException {
    Path zipFile = Path.of("files/one.zip");
    try (FileSystem fileSystem = FileSystems.newFileSystem(zipFile)) {
      Path path = fileSystem.getPath("one/two.txt");
      List<String> strings = Files.readAllLines(path);
      System.out.println(strings);
    }
  }

  @Test
  void readHttpJar() throws IOException {
    URL jarUrl = new URL("jar:https://repo.maven.apache.org/maven2/com/google/guava/guava/10.0/guava-10.0-javadoc.jar!/package-list");
    JarURLConnection urlConnection = (JarURLConnection) jarUrl.openConnection();

    InputStream inputStream = urlConnection.getInputStream();
    byte[] byteArray = ByteStreams.toByteArray(inputStream);
    String fileContent = new String(byteArray, StandardCharsets.UTF_8);
    System.out.println(fileContent);


  }

}
