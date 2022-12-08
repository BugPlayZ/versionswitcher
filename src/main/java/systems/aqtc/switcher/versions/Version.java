package systems.aqtc.switcher.versions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Version {
  private final String name;
  private final String version;
  private final String companyName;
  private final String fileDescription;
  private final String path;
}
