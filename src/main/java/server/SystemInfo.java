package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SystemInfo {
    public static boolean hasAMDGPU() {
        try {
            ProcessBuilder pb = new ProcessBuilder("wmic", "path", "win32_VideoController", "get", "name");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("amd") || line.toLowerCase().contains("radeon")) {
                    return true;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
