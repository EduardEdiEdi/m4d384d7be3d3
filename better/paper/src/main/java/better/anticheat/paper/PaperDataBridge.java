package better.anticheat.paper;

import java.util.UUID;
import org.bukkit.plugin.java.JavaPlugin;

import better.anticheat.core.DataBridge;
import better.anticheat.core.player.tracker.impl.confirmation.ConfirmationState;
import better.anticheat.core.player.tracker.impl.confirmation.PlatformNativeConfirmationHandler;
import com.github.retrooper.packetevents.protocol.player.User;
import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/*
 * Modified: works for any host JavaPlugin, no permissions/commands, no GrimAC hook; fails go to a Reporter.
 */
public class PaperDataBridge implements DataBridge {

    /** Receives every reported fail (any thread). */
    public interface Reporter {
        void report(UUID player, String category, String check, int vl, String debug);
    }

    private final JavaPlugin plugin;
    private final FoliaLib lib;
    private final Reporter reporter;

    public PaperDataBridge(JavaPlugin plugin, Reporter reporter) {
        this.plugin = plugin;
        this.reporter = reporter;
        lib = new FoliaLib(plugin);
    }

    @Override
    public void logInfo(String message) {
        plugin.getLogger().info("[combat] " + message);
    }

    @Override
    public void logWarning(String message) {
        plugin.getLogger().warning("[combat] " + message);
    }

    @Override
    public void report(User user, String category, String check, int vl, String debug) {
        if (user == null || user.getUUID() == null) return;
        reporter.report(user.getUUID(), category, check, vl, debug);
    }

    @Override
    public Closeable registerTickListener(User user, Runnable runnable) {
        if (user.getUUID() == null) return null;
        var player = Bukkit.getPlayer(user.getUUID());
        if (player != null) {
            final var task = lib.getScheduler().runAtEntityTimer(player, runnable, 1, 1);
            if (task != null) {
                return task::cancel;
            }
        }

        final var future = new CompletableFuture<Object>();
        final var cancelled = new AtomicBoolean(false);
        lib.getScheduler().runAsync(task -> {
            var cycles = 0;
            while (!cancelled.get()) {
                var p = Bukkit.getPlayer(user.getUUID());
                if (p != null) {
                    final var innerTask = lib.getScheduler().runAtEntityTimer(p, runnable, 1, 1);
                    if (innerTask != null) {
                        future.whenComplete((a, b) -> innerTask.cancel());
                        return;
                    }
                }
                try {
                    Thread.sleep(50);
                    // 50 Seconds MAX
                    if (cycles++ > 1000) {
                        return;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        return () -> {
            cancelled.set(true);
            future.complete(new Object());
        };
    }

    @Override
    public Closeable runTaskLater(User user, Runnable runnable, int delayTicks) {
        if (user.getUUID() == null) return null;
        var player = Bukkit.getPlayer(user.getUUID());
        if (player != null) {
            final var task = lib.getScheduler().runAtEntityLater(player, runnable, delayTicks);
            if (task != null) {
                return task::cancel;
            }
        }

        final var future = new CompletableFuture<Object>();
        final var cancelled = new AtomicBoolean(false);
        lib.getScheduler().runAsync(task -> {
            var cycles = 0;
            while (!cancelled.get()) {
                var p = Bukkit.getPlayer(user.getUUID());
                if (p != null) {
                    final var innerTask = lib.getScheduler().runAtEntityLater(p, runnable, delayTicks);
                    if (innerTask != null) {
                        future.whenComplete((a, b) -> innerTask.cancel());
                        return;
                    }
                }
                try {
                    Thread.sleep(50);
                    // 50 Seconds MAX
                    if (cycles++ > 1000) {
                        return;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        return () -> {
            cancelled.set(true);
            future.complete(new Object());
        };
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }
}
