package better.anticheat.core.player.tracker.impl;

import better.anticheat.core.player.Player;
import better.anticheat.core.player.tracker.Tracker;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import lombok.Getter;

/**
 * This tracker handles basic data tracking regarding a player's position. This is a centralized way to access a
 * player's latest position at any given time.
 */
@Getter
public class PositionTracker extends Tracker {

    private boolean position, lastPosition;
    private double x, y, z, lastX, lastY, lastZ;
    private double deltaX, deltaY, deltaZ, deltaXZ, lastDeltaX, lastDeltaY, lastDeltaZ, lastDeltaXZ;
    private long ticks;

    public PositionTracker(Player player) {
        super(player);
    }

    /*
     * Packet handling.
     */

    @Override
    public void handlePacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) ticks++;
        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) return;
        WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

        lastPosition = position;
        position = wrapper.hasPositionChanged();
        if (!position) return;

        lastX = x;
        lastY = y;
        lastZ = z;
        lastDeltaX = deltaX;
        lastDeltaY = deltaY;
        lastDeltaZ = deltaZ;
        lastDeltaXZ = deltaXZ;

        x = wrapper.getLocation().getX();
        y = wrapper.getLocation().getY();
        z = wrapper.getLocation().getZ();
        deltaX = x - lastX;
        deltaY = y - lastY;
        deltaZ = z - lastZ;
        deltaXZ = Math.hypot(deltaX, deltaZ);
    }
}
