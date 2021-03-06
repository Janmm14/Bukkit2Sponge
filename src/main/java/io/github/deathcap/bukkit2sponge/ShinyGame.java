package io.github.deathcap.bukkit2sponge;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.deathcap.bukkit2sponge.command.ShinyCommandService;
import io.github.deathcap.bukkit2sponge.event.ShinyEventManager;
import io.github.deathcap.bukkit2sponge.plugin.ShinyPluginManager;
import io.github.deathcap.bukkit2sponge.text.ShinyTextFactory;
import org.bukkit.Bukkit;
import org.spongepowered.api.*;
import org.spongepowered.api.service.ServiceManager;
import org.spongepowered.api.service.SimpleServiceManager;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.command.SimpleCommandService;
import org.spongepowered.api.service.scheduler.AsynchronousScheduler;
import org.spongepowered.api.service.scheduler.SynchronousScheduler;
import org.spongepowered.api.world.TeleportHelper;

/**
 * Implementation of {@link Game}.
 */

@Singleton
public class ShinyGame implements Game {


    private final ShinyPluginManager pluginManager = new ShinyPluginManager(this);
    private final ShinyEventManager eventManager = new ShinyEventManager();
    private final ShinyGameRegistry registry = new ShinyGameRegistry();
    private final SimpleServiceManager services = new SimpleServiceManager(pluginManager);
    private final SimpleCommandService commands = new ShinyCommandService(this);
    private final ShinyServer server = new ShinyServer(Bukkit.getServer());

    private static final String API_VERSION;
    private static final String IMPL_VERSION;

    static {
        Package pkg = ShinyGame.class.getPackage();
        String apiVersion = pkg.getSpecificationVersion();
        API_VERSION = (apiVersion == null) ? "unknown" : apiVersion;
        String implVersion = pkg.getImplementationVersion();
        IMPL_VERSION = (implVersion == null) ? "unknown" : implVersion;
        ShinyTextFactory.inject();
    }

    @Inject
    public ShinyGame() {

    }

    // platform information

    @Override
    public Platform getPlatform() {
        return Platform.SERVER;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public String getImplementationVersion() {
        return IMPL_VERSION;
    }

    @Override
    public MinecraftVersion getMinecraftVersion() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TeleportHelper getTeleportHelper() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    // service access

    @Override
    public ShinyPluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public ShinyEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public GameRegistry getRegistry() {
        return registry;
    }

    @Override
    public ServiceManager getServiceManager() {
        return services;
    }

    @Override
    public SynchronousScheduler getSyncScheduler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AsynchronousScheduler getAsyncScheduler() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CommandService getCommandDispatcher() {
        return commands;
    }

    @Override
    public String getApiVersion() {
        return API_VERSION;
    }
}
