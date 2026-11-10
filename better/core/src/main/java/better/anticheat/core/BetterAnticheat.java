package better.anticheat.core;

import better.anticheat.core.check.CheckManager;
import better.anticheat.core.configuration.ConfigSection;
import better.anticheat.core.configuration.ConfigurationFile;
import better.anticheat.core.player.PlayerManager;
import better.anticheat.core.player.tracker.impl.confirmation.cookie.CookieAllocatorConfig;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import lombok.Getter;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
/*
 * Modified: report-only core. Commands, punishments, alerts, webhooks, machine-learning models, recording and combat
 * mitigation were removed; checks report to the host through DataBridge#report.
 */
public class BetterAnticheat {

    @Getter
    private static BetterAnticheat instance;

    // Constructor-related objs
    private final DataBridge dataBridge;
    private final Path directory;
    private boolean enabled;

    // Managers
    private final CheckManager checkManager;
    private final PlayerManager playerManager;

    // Settings
    private boolean ignorePre121Players;
    private CookieAllocatorConfig cookieAllocatorConfig;
    private boolean entityTrackerFastAwaitingUpdate;
    private boolean entityTrackerFastEntityBox;

    public BetterAnticheat(DataBridge dataBridge, Path directory) {
        this.dataBridge = dataBridge;
        this.directory = directory;
        this.enabled = true;

        instance = this;

        this.checkManager = new CheckManager(this);
        this.playerManager = new PlayerManager(this);

        /*
         * We only support 1.21+.
         */
        if (PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_21)) {
            dataBridge.logWarning("You are running on an unsupported version of Minecraft!");
            dataBridge.logWarning("Please update to 1.21 or above!");
            enabled = false;
        }
    }

    public void enable() {
        if (!enabled) return;
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener(this));
        load();

        // Ensure players are 1.21+. We will conditionally load checks depending on features (e.g., CLIENT_TICK_END).
        playerManager.registerQuantifier((user -> !ignorePre121Players || user.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21)));
    }

    public void disable() {
        enabled = false;
    }

    public void load() {
        if (!enabled) return;

        dataBridge.logInfo("Beginning load!");

        ConfigSection settings = getFile("settings.yml", BetterAnticheat.class.getResourceAsStream("/settings.yml")).load();
        ignorePre121Players = settings.getObject(Boolean.class, "dont-inject-pre-121-players", true);

        // This is true in the default config but we set it to false here so people updating their server without knowing about the new config do not get fucked.
        this.entityTrackerFastAwaitingUpdate = settings.getObject(Boolean.class, "entity-tracker.fast-awaiting-update", false);
        this.entityTrackerFastEntityBox = settings.getObject(Boolean.class, "entity-tracker.fast-entity-box", false);

        loadCookieAllocator(settings);

        checkManager.load();
        playerManager.load();

        dataBridge.logInfo("Load finished!");
    }

    /**
     * Loads the cookie allocator configuration from the provided base configuration.
     *
     * @param baseConfig The base configuration section.
     */
    public void loadCookieAllocator(final ConfigSection baseConfig) {
        final var cookieNode = baseConfig.getConfigSection("cookie-allocator");

        if (cookieNode == null) {
            dataBridge.logInfo("No cookie allocator configuration found, using default sequential allocator");
            this.cookieAllocatorConfig = CookieAllocatorConfig.createDefault();
            return;
        }

        final var type = cookieNode.getObject(String.class, "type", "sequential");
        final var parametersNode = cookieNode.getConfigSection("parameters");

        final Map<String, Object> parameters = new HashMap<>();
        if (parametersNode != null) {

            // For sequential allocator
            if (parametersNode.hasNode("startValue")) {
                parameters.put("startValue", parametersNode.getObject(Long.class, "startValue", 0L));
            }

            // For random allocator
            if (parametersNode.hasNode("cookieLength")) {
                parameters.put("cookieLength", parametersNode.getObject(Integer.class, "cookieLength", 8));
            }
            if (parametersNode.hasNode("maxRetries")) {
                parameters.put("maxRetries", parametersNode.getObject(Integer.class, "maxRetries", 100));
            }

            // For timestamp allocator
            if (parametersNode.hasNode("randomBytesLength")) {
                parameters.put("randomBytesLength", parametersNode.getObject(Integer.class, "randomBytesLength", 4));
            }

        }

        this.cookieAllocatorConfig = new CookieAllocatorConfig(type, parameters);

        dataBridge.logInfo("Loaded cookie allocator configuration: type=" + type + ", parameters=" + parameters);
    }

    public ConfigurationFile getFile(String name) {
        return new ConfigurationFile(name, directory);
    }

    public ConfigurationFile getFile(String name, InputStream input) {
        return new ConfigurationFile(name, directory, input);
    }

}
