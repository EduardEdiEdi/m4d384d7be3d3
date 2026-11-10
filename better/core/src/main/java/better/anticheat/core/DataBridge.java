package better.anticheat.core;

import better.anticheat.core.player.tracker.impl.confirmation.PlatformNativeConfirmationHandler;
import com.github.retrooper.packetevents.protocol.player.User;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;

/*
 * Modified: commands, permissions and console commands removed. Fails go to the host through report(...).
 */
public interface DataBridge {

    void logInfo(String message);

    void logWarning(String message);

    /** A check failed at or above its report VL. May be called from a netty thread. */
    void report(User user, String category, String check, int vl, @Nullable String debug);

    @Nullable Closeable registerTickListener(User user, Runnable runnable);
    @Nullable Closeable runTaskLater(User user, Runnable runnable, int delayTicks);

    default PlatformNativeConfirmationHandler getNativeConfirmationHandler() {
        return null;
    }

    String getVersion();
}
