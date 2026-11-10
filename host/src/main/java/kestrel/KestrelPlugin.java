package kestrel;

import better.anticheat.paper.CombatModule;
import kestrel.api.DetectionEvent;
import me.vekster.lightanticheat.Main;
import me.vekster.lightanticheat.util.violation.ViolationHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * One plugin, two detection modules, report-only. The interaction module is the LightAntiCheat code base (this class
 * extends its main class), the combat module is the BetterAnticheat code base. Every detection becomes a
 * {@link DetectionEvent} on the main thread; nothing here alerts, kicks, bans, sets back or changes damage.
 */
public final class KestrelPlugin extends Main {

    private static final long MIN_GAP_MS = 200;

    private final CombatModule combat = new CombatModule();
    private final Map<String, Long> last = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        ViolationHandler.setReporter((id, category, check, level) -> fire(id, "interaction", category, check, level, null));
        super.onEnable();
        try {
            combat.start(this, (id, category, check, level, debug) -> fire(id, "combat", category, check, level, debug));
        } catch (Throwable t) {
            getLogger().warning("Combat module unavailable: " + t);
        }
        getLogger().info("Detection modules running (report-only).");
    }

    @Override
    public void onDisable() {
        ViolationHandler.setReporter(null);
        try {
            combat.stop();
        } catch (Throwable ignored) {
        }
        super.onDisable();
        last.clear();
    }

    private void fire(UUID id, String module, String category, String check, int level, String detail) {
        long now = System.currentTimeMillis();
        String key = id + "|" + check;
        Long prev = last.put(key, now);
        if (prev != null && now - prev < MIN_GAP_MS) return;
        if (!isEnabled()) return;
        Runnable r = () -> {
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline()) return;
            Bukkit.getPluginManager().callEvent(new DetectionEvent(p, module, category, check, level, detail));
        };
        if (Bukkit.isPrimaryThread()) r.run();
        else Bukkit.getScheduler().runTask(this, r);
    }
}
