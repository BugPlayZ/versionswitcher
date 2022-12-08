package systems.aqtc.switcher;

import java.util.List;
import java.util.Scanner;
import systems.aqtc.switcher.utils.VersionUtils;
import systems.aqtc.switcher.versions.Version;
import systems.aqtc.switcher.versions.VersionController;

public enum VersionSwitcher {
  INSTANCE;

  private VersionController versionController;

  public void start() {
    System.out.println(
        " /$$    /$$ /$$$$$$$$ /$$$$$$$   /$$$$$$  /$$$$$$  /$$$$$$  /$$   /$$  /$$$$$$ \n"
            + "| $$   | $$| $$_____/| $$__  $$ /$$__  $$|_  $$_/ /$$__  $$| $$$ | $$ /$$__  $$\n"
            + "| $$   | $$| $$      | $$  \\ $$| $$  \\__/  | $$  | $$  \\ $$| $$$$| $$| $$  \\__/\n"
            + "|  $$ / $$/| $$$$$   | $$$$$$$/|  $$$$$$   | $$  | $$  | $$| $$ $$ $$|  $$$$$$ \n"
            + " \\  $$ $$/ | $$__/   | $$__  $$ \\____  $$  | $$  | $$  | $$| $$  $$$$ \\____  $$\n"
            + "  \\  $$$/  | $$      | $$  \\ $$ /$$  \\ $$  | $$  | $$  | $$| $$\\  $$$ /$$  \\ $$\n"
            + "   \\  $/   | $$$$$$$$| $$  | $$|  $$$$$$/ /$$$$$$|  $$$$$$/| $$ \\  $$|  $$$$$$/\n"
            + "    \\_/    |________/|__/  |__/ \\______/ |______/ \\______/ |__/  \\__/ \\______/ \n"
            + "                                                                               \n");

    System.out.println("\nInitializing VersionController...");
    versionController = new VersionController();
    System.out.println("VersionController initialized!");

    System.out.println("\nWelcome, " + System.getProperty("user.name"));
    System.out.println("Current Java-Version " + versionController.getJavaVersion());
    System.out.println("Current Architecture: " + versionController.getSystemArchitecture());

    if (versionController.getSystemArchitecture() == 64) {
      System.out.println("Architecture is 64-bit, continuing...");
    } else {
      System.out.println("Architecture is not 64-bit, exiting...");
      System.exit(1);
    }

    System.out.println("\nLoading versions, this may take a while...");
    List<Version> versions = VersionUtils.filterDuplicates(versionController.loadVersions());

    System.out.println("Found " + versions.size() + " versions!");

    int id = 0;
    for (Version version : versions) {
      System.out.println(
          "[" + (++id) + "] " + version.getFileDescription() + " - " + version.getVersion() + " - " + version.getPath());
    }

    do {
        System.out.print("\nPlease enter the ID of the version you want to switch to: ");
        String input = new Scanner(System.in).nextLine();

        try {
            int index = Integer.parseInt(input);
            Version version = versions.get(index - 1);

            if (version == null) {
                System.out.println("Invalid ID!");
                continue;
            }

            System.out.println("Switching to " + version.getFileDescription() + " - " + version.getVersion());

            if (versionController.switchVersion(version)) {
                System.out.println("Successfully switched to " + version.getFileDescription() + " - " + version.getVersion());
                System.exit(0);
            } else {
                System.out.println("Failed to switch to " + version.getFileDescription() + " - " + version.getVersion());
                continue;
            }
            break;
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    } while (true);
  }
}
