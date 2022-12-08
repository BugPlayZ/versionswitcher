package systems.aqtc.switcher.utils;

import com.kichik.pecoff4j.PE;
import com.kichik.pecoff4j.ResourceDirectory;
import com.kichik.pecoff4j.ResourceEntry;
import com.kichik.pecoff4j.constant.ResourceType;
import com.kichik.pecoff4j.io.PEParser;
import com.kichik.pecoff4j.io.ResourceParser;
import com.kichik.pecoff4j.resources.StringFileInfo;
import com.kichik.pecoff4j.resources.StringTable;
import com.kichik.pecoff4j.resources.VersionInfo;
import com.kichik.pecoff4j.util.ResourceHelper;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.VerRsrc;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import systems.aqtc.switcher.versions.Version;

public class WindowsUtils {

  public static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  @SneakyThrows
  @Deprecated
  public static String getFileVersionInfo(String path) {
    File fileToCheck = new File(path);
    short[] rtnData = new short[4];
    int infoSize = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(fileToCheck.getAbsolutePath(), null);
    Pointer buffer = Kernel32.INSTANCE.LocalAlloc(WinBase.LMEM_ZEROINIT, infoSize);

    try {

      com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(fileToCheck.getAbsolutePath(), 0, infoSize, buffer);

      IntByReference outputSize = new IntByReference();
      PointerByReference pointer = new PointerByReference();

      com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(buffer, "\\", pointer, outputSize);

      VerRsrc.VS_FIXEDFILEINFO fileInfoStructure = new VerRsrc.VS_FIXEDFILEINFO(pointer.getValue());

      rtnData[0] = (short) (fileInfoStructure.dwProductVersionMS.longValue() >> 16);
      rtnData[1] = (short) (fileInfoStructure.dwProductVersionMS.longValue() & 0xffff);
      rtnData[2] = (short) (fileInfoStructure.dwProductVersionLS.longValue() >> 16);
      rtnData[3] = (short) (fileInfoStructure.dwProductVersionLS.longValue() & 0xffff);
    } finally {
      Kernel32.INSTANCE.GlobalFree(buffer);
    }

    return String.join(".", convertToString(rtnData));
  }

  @SneakyThrows
  public static Map<String, String> getDetails(String path) {
    PE pe = PEParser.parse(path);
    ResourceDirectory rd = pe.getImageData().getResourceTable();

    ResourceEntry[] entries = ResourceHelper.findResources(rd, ResourceType.VERSION_INFO);
    Map<String, String> details = new HashMap<>();

    for (ResourceEntry entry : entries) {
      byte[] data = entry.getData();
      VersionInfo version = ResourceParser.readVersionInfo(data);

      StringFileInfo strings = version.getStringFileInfo();
      StringTable table = strings.getTable(0);

      for (int j = 0; j < table.getCount(); j++) {
        String key = table.getString(j).getKey();
        String value = table.getString(j).getValue();

        details.put(key, value);
      }
    }

    return details;
  }

  private static String[] convertToString(short[] array) {
    String[] parts = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      parts[i] = String.valueOf(array[i]);
    }
    return parts;
  }
}
