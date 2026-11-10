package better.anticheat.paper;

import better.anticheat.core.BetterAnticheat;
import io.github.retrooper.packetevents.util.GeyserUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

/*
 * Modified: replaces the standalone plugin class. The module runs inside a host plugin (no bStats, no commands,
 * no reload hook); its files live in <host data folder>/combat.
 */
public final class CombatModule {

    private BetterAnticheat core;

    public void start(JavaPlugin host, PaperDataBridge.Reporter reporter) {
        Path dir = host.getDataFolder().toPath().resolve("combat");
        dir.toFile().mkdirs();
        core = new BetterAnticheat(new PaperDataBridge(host, reporter), dir);
        core.enable();
        core.getPlayerManager().registerQuantifier(user -> !GeyserUtil.isGeyserPlayer(user.getUUID()));
    }

    public void stop() {
        if (core != null) core.disable();
    }
}
