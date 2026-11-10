package me.vekster.lightanticheat.util.violation;

import me.vekster.lightanticheat.check.CheckSetting;
import me.vekster.lightanticheat.player.LACPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

/*
 * Modified: no alerts, logs, webhooks, setbacks or punishments. Every violation is counted and handed to the host
 * plugin through the Reporter; the host decides what it means.
 */
public class ViolationHandler implements Listener {

    /** Receives every violation (may be called off the main thread). */
    public interface Reporter {
        void report(UUID player, String category, String check, int violations);
    }

    private static volatile Reporter reporter;

    public static void setReporter(Reporter r) {
        reporter = r;
    }

    public static void flag(CheckSetting checkSetting, Player player, LACPlayer lacPlayer) {
        if (player == null || lacPlayer == null || !player.isOnline() || lacPlayer.leaveTime != 0L) return;
        lacPlayer.violations.increaseViolations(checkSetting.name, 1);
        Reporter r = reporter;
        if (r == null) return;
        r.report(player.getUniqueId(), checkSetting.name.type.name().toLowerCase(), checkSetting.name.title,
                lacPlayer.violations.getViolations(checkSetting.name));
    }
}
