package com.essaid.google.harmonization.extensions;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.junit.jupiter.api.Test;

public class PathUriUrlExamples {

  @Test
  void getJarFromHttp() throws IOException {
    URL httpUrl = new URL("jar:https://repo.maven.apache.org/maven2/com/google/guava/guava/10.0/guava-10.0-javadoc.jar!/");
    JarURLConnection.setDefaultUseCaches("https", true);
    JarURLConnection jarConnection = (JarURLConnection) httpUrl.openConnection();
    jarConnection.setUseCaches(true);
    JarFile jarFile = jarConnection.getJarFile();

    byte[] byteArray = ByteStreams.toByteArray(jarFile.getInputStream(new ZipEntry("package-list")));
    String filecontent = new String(byteArray, StandardCharsets.UTF_8);
    System.out.println(jarFile);
    System.out.println(jarFile);

  }

  @Test
  void urlTrailingSlash() throws MalformedURLException {
    URL url = new URL("http://google.com/");
    String string = url.toString();
    System.out.println(url);


  }

}
