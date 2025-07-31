package me.pilkeysek.skyenetv.api;

import com.velocitypowered.api.proxy.ProxyServer;

/**
 * SkyeNetV API Provider
 * Use this class to get access to the SkyeNetV API from other plugins
 */
public class SkyeNetVProvider {
    
    private static SkyeNetVAPI api;
    
    /**
     * Initialize the API provider (called internally by SkyeNetV)
     * 
     * @param server The proxy server instance
     */
    public static void initialize(ProxyServer server) {
        api = new SkyeNetVAPIImpl(server);
    }
    
    /**
     * Get the SkyeNetV API instance
     * 
     * @return The API instance, or null if SkyeNetV is not loaded
     */
    public static SkyeNetVAPI getAPI() {
        return api;
    }
    
    /**
     * Check if the SkyeNetV API is available
     * 
     * @return true if the API is available
     */
    public static boolean isAvailable() {
        return api != null;
    }
}
