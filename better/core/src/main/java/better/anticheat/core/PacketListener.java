package better.anticheat.core;

import better.anticheat.core.player.Player;
import better.anticheat.core.player.PlayerManager;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PacketListener extends SimplePacketListenerAbstract {

    private final BetterAnticheat plugin;

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        Player player = plugin.getPlayerManager().getPlayer(event.getUser());
        if (player == null) return;

        try {
            player.handleReceivePacket(event);
        } catch (final Exception e) {
            // report-only: a module error never costs the player their connection
            log.debug("Error while handling packet (i) for {}: ", event.getUser().getName(), e);
        }
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {
            plugin.getPlayerManager().addUser(event.getUser(), plugin.getDataBridge());
            return;
        }

        Player player = plugin.getPlayerManager().getPlayer(event.getUser());
        if (player == null) return;
        try {
            player.handleSendPacket(event);
        } catch (final Exception e) {
            log.debug("Error while handling packet (o) for {}: ", event.getUser().getName(), e);
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        try {
            plugin.getPlayerManager().removeUser(event.getUser());
        } catch (final Exception e) {
            log.error("Error while removing player: ", e);
        }
    }
}
