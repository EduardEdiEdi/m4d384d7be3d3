package better.anticheat.core.check;

import better.anticheat.core.player.Player;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

/**
 * Enumerates client feature requirements for checks. Each constant knows how to evaluate support on a Player.
 */
public enum ClientFeatureRequirement {
    /**
     * Requires the client to support CLIENT_TICK_END packet (introduced in 1.21.2).
     */
    CLIENT_TICK_END {
        @Override
        public boolean matches(Player player) {
            // 1.21.2 and newer have CLIENT_TICK_END
            return player.getUser().getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_21_2);
        }
    };

    public abstract boolean matches(Player player);
}