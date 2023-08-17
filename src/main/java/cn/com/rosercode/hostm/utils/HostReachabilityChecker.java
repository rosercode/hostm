package cn.com.rosercode.hostm.utils;

import java.net.InetAddress;


/**
 * @author rosercode
 * @date 2023/8/17 14:19
 */

public class HostReachabilityChecker {
    /**
     * 判断 ip address 是否可达
     * @param ipAddress
     * @return
     */
    public static boolean isReachable(String ipAddress) {
        int timeoutMillis = 5000;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            return inetAddress.isReachable(timeoutMillis);
        } catch (Exception e) {
            return false;
        }
    }

}
