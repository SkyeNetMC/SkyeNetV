package me.pilkeysek.skyeNetV;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.slf4j.Logger;

@Plugin(id = "skyenetv", name = "SkyeNetV", version = BuildConstants.VERSION, description = "Velocity Plugin for SkyeNet", url = "store.skyenet.co.in", authors = {"PilkeySEK"})
public class SkyeNetV {

    @Inject
    private Logger logger;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}
