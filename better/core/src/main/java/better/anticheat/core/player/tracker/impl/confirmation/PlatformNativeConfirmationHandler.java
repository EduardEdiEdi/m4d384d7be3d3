package better.anticheat.core.player.tracker.impl.confirmation;

import better.anticheat.core.player.Player;

import java.util.function.Consumer;

public interface PlatformNativeConfirmationHandler {
    /**
     * Sends a transaction to the native confirmation system.
     */
    ConfirmationState confirmPost(Player player, Consumer<ConfirmationState> runnable);
}
