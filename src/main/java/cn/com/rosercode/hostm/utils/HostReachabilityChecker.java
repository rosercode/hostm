package cn.com.rosercode.hostm.utils;

import java.io.IOException;
import java.net.InetAddress;


/**
 * @author rosercode
 * @date 2023/8/17 14:19
 */

public class HostReachabilityChecker {
    /**
     * 判断 ip address 是否可达
     *
     * @param ipAddress
     * @return
     */
    public static boolean isReachable(String ipAddress) {
        // 先调用系统命令来判断主机是否可达
        if (ping(ipAddress)){
            return true;
        }
        int timeoutMillis = 5000;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isReachable(timeoutMillis);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean ping(String ipAddress) {

        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder processBuilder;
        if (os.contains("win")) {
            // Windows
            processBuilder = new ProcessBuilder("ping", "-n", "4", ipAddress);
        } else {
            // Linux and others
            processBuilder = new ProcessBuilder("ping", "-c", "4", ipAddress);
        }
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
