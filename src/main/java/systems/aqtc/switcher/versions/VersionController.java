package systems.aqtc.switcher.versions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import systems.aqtc.switcher.utils.WindowsUtils;

public class VersionController {

  private static final File[] PATHS = {new File("C:\\Program Files\\"),
      new File(System.getProperty("user.home"))};

  public String getJavaVersion() {
    return System.getProperty("java.version");
  }

  public int getSystemArchitecture() {
    return Integer.parseInt(System.getProperty("sun.arch.data.model"));
  }

  @SneakyThrows
  public List<Version> loadVersions() {
    List<Version> versions = new ArrayList<>();

    for (File folder : PATHS) {
      for (File file : Objects.requireNonNull(folder.listFiles())) {

        if (!file.isDirectory()) {
          continue;
        }

        List<File> javaFiles = this.searchFolder(file, "java.exe");
        for (File javaFile : javaFiles) {
          Map<String, String> details = WindowsUtils.getDetails(javaFile.getAbsolutePath());
          Version version = new Version(details.get("ProductName"), details.get("FileVersion"),
              details.get("CompanyName"), details.get("FileDescription"),
              javaFile.getAbsolutePath());

          versions.add(version);
        }
      }
    }

    return versions;
  }

  private List<File> searchFolder(File folder, String fileName) {
    List<File> javaFiles = new ArrayList<>();

    for (File file : (Objects.requireNonNull(
        folder.listFiles() == null ? new File[0] : folder.listFiles()))) {
      if (file == null) {
        continue;
      }

      if (file.isDirectory()) {
        javaFiles.addAll(this.searchFolder(file, fileName));
      } else if (file.getName().equals(fileName)) {
        javaFiles.add(file);
      }
    }

    return javaFiles;
  }

  public boolean switchVersion(Version version) {
    try {
      // TODO: Fix 'PATH' environment variable

      Runtime.getRuntime().exec("cmd.exe /c assoc .jar=jarfile");
      Runtime.getRuntime().exec("cmd.exe /c ftype jarfile=\"" + version.getPath() + "\" \"%1\"");
      Runtime.getRuntime().exec("cmd.exe /c JAVA_HOME=" + version.getPath());

      String[] paths = System.getenv("PATH").split(";");
      List<String> newPaths = new ArrayList<>();
      for (String path : paths) {
        if (path.contains("java") || path.contains("jdk") || path.contains("jre")) {
          continue;
        }

        newPaths.add(path);
      }

      System.setProperty("Path", String.join(";", newPaths));

      Runtime.getRuntime().exec("cmd.exe /c setx PATH \"" + String.join(";", newPaths) + "\"");
      Runtime.getRuntime().exec("cmd.exe /c set PATH \"" + String.join(";", newPaths) + "\"");
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
