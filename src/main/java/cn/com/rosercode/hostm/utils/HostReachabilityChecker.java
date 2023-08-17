package cn.com.rosercode.hostm.utils;

import java.io.IOException;
import java.net.InetAddress;


/**
 * @author rosercode
 * @date 2023/8/17 14:19
 */

public class HostReachabilityChecker {
    private static final int PING_TIMEOUT = 5000;
    private static final int PING_RETRIES = 4;

    public static boolean isReachable(String ipAddress) {
        if (ping(ipAddress)) {
            return true;
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isReachable(PING_TIMEOUT);
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean ping(String ipAddress) {
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;

        if (os.contains("win")) {
            // Windows
            processBuilder = new ProcessBuilder("ping", "-n", String.valueOf(PING_RETRIES), ipAddress);
        } else {
            // Linux and others
            processBuilder = new ProcessBuilder("ping", "-c", String.valueOf(PING_RETRIES), ipAddress);
        }

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}