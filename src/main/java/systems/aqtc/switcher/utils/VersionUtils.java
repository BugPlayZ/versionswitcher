package systems.aqtc.switcher.utils;

import java.util.ArrayList;
import java.util.List;
import systems.aqtc.switcher.versions.Version;

public class VersionUtils {

  public static List<Version> filterDuplicates(List<Version> versions) {
    List<Version> filtered = new ArrayList<>();

    for (Version version : versions) {
      if (filtered.stream().noneMatch(v -> v.getName().equals(version.getName()) && v.getVersion()
          .equals(version.getVersion()))) {
        filtered.add(version);
      }
    }

    return filtered;
  }
}
