
package io.github.deathcap.bukkit2sponge;

import com.google.common.base.Throwables;
import com.google.common.io.PatternFilenameFilter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.deathcap.bukkit2sponge.bukkit.BukkitListener;
import io.github.deathcap.bukkit2sponge.event.GraniteEventFactory;
import io.github.deathcap.bukkit2sponge.guice.ShinyGuiceModule;
import io.github.deathcap.bukkit2sponge.plugin.GlowstonePlusPlusConnector;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.event.state.ConstructionEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.StateEvent;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bukkit2Sponge extends JavaPlugin {

    public static Bukkit2Sponge instance = null;
    private final BukkitListener bukkitListener = new BukkitListener();

    public Injector getInjector() {
        return injector;
    }

    private Injector injector;

    private ShinyGame game;

    @Override
    public void onDisable() {
        getLogger().info("Goodbye world!");
    }

    @Override
    public void onEnable() {
        Bukkit2Sponge.instance = this;


        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is loading");

        load();
    }

    private List<URL> getPluginURLs() {
        // Our own personal plugins directory
        File directory = getPluginsDirectory();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File[] files = directory.listFiles(new PatternFilenameFilter(".+\\.jar"));

        List<URL> urls = new ArrayList<>(files.length);
        for (File jar : files) {
            try {
                urls.add(jar.toURI().toURL());
            } catch (MalformedURLException e) {
                Bukkit2Sponge.instance.getLogger().warning("Malformed URL: " + jar + e);
            }
        }

        List<URL> gs = GlowstonePlusPlusConnector.getSpongePlugins(getServer());
        if (gs != null) {
            Bukkit2Sponge.instance.getLogger().info("Glowstone++ integration enabled (" + gs.size() + " plugins)");
            urls.addAll(gs);
        }

        return urls;
    }

    private void load() {
        Collection<URL> loadedPluginURLs = null;
        List<URL> urls = getPluginURLs();

        try {
            injector = Guice.createInjector(new ShinyGuiceModule());

            /*
             CONSTRUCTION,
             LOAD_COMPLETE,
             PRE_INITIALIZATION,
             INITIALIZATION,
             POST_INITIALIZATION,
             SERVER_ABOUT_TO_START,
             SERVER_STARTING,
             SERVER_STARTED,
             SERVER_STOPPING,
             SERVER_STOPPED
             */


            this.game = injector.getInstance(ShinyGame.class);

            getServer().getPluginManager().registerEvents(bukkitListener, this);

            getLogger().info("SpongeAPI version: " + this.game.getApiVersion());

            getLogger().info("Loading plugins...");
            loadedPluginURLs = this.game.getPluginManager().loadPlugins(urls);
            postState(ConstructionEvent.class);
            getLogger().info("Initializing " + loadedPluginURLs.size() + " SpongeAPI plugins...");
            postState(PreInitializationEvent.class);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public ShinyGame getGame() {
        return this.game;
    }

    public void postState(Class<? extends StateEvent> type) {
        this.game.getEventManager().post(GraniteEventFactory.createStateEvent(type, this.game));
    }

    // Sponge directories relative to our own Bukkit plugin data folder

    public File getPluginsDirectory() {
        return new File(this.getDataFolder(), "plugins");
    }

    public File getConfigDirectory() {
        return new File(this.getDataFolder(), "config");
    }
}