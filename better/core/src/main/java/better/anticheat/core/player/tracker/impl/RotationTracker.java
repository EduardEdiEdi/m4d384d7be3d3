package better.anticheat.core.player.tracker.impl;

import better.anticheat.core.player.Player;
import better.anticheat.core.player.tracker.Tracker;
import better.anticheat.core.util.MathUtil;
import better.anticheat.core.util.math.GraphUtil;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import lombok.Getter;

import java.util.List;

/**
 * This tracker handles basic data tracking regarding a player's rotation. This is a centralized way to access a
 * player's latest rotation at any given time.
 */
public class RotationTracker extends Tracker {

    private static final double CINEMATIC_CONSTANT = 0.0078125F;

    private final List<Float> pitchSamples = new FloatArrayList(), yawSamples = new FloatArrayList();
    private int ticksSinceHigh = 0, ticksSinceSmooth = 0, cinematicTicks = 0, internalTicksSinceCinematic = 0;

    @Getter
    private boolean rotation, lastRotation, cinematic, cinematic2;
    @Getter
    private float pitch, yaw, lastPitch, lastYaw;
    @Getter
    private float deltaPitch, deltaYaw, lastDeltaPitch, lastDeltaYaw;
    @Getter
    private int ticksSinceCinematic = 0;

    public RotationTracker(Player player) {
        super(player);
    }

    /*
     * Packet handling.
     */

    @Override
    public void handlePacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) {
            ticksSinceHigh++;
            ticksSinceSmooth++;
            internalTicksSinceCinematic++;
            ticksSinceCinematic++;
            return;
        }

        if (!WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) return;
        WrapperPlayClientPlayerFlying wrapper = new WrapperPlayClientPlayerFlying(event);

        lastRotation = rotation;
        rotation = wrapper.hasRotationChanged();
        if (!rotation) return;

        lastPitch = pitch;
        lastYaw = yaw;
        lastDeltaPitch = deltaPitch;
        lastDeltaYaw = deltaYaw;

        pitch = wrapper.getLocation().getPitch();
        yaw = wrapper.getLocation().getYaw();
        deltaPitch = pitch - lastPitch;
        deltaYaw = yaw - lastYaw;

        processCinematic();
    }

    /**
     * This method will determine whether the player appears to be using cinematic mode right now.
     * This cinematic processor is from Artemis. Artemis is licensed under MIT so we can use it.
     * It appears to be relatively accurate, so it'll do well enough for us!
     */
    private void processCinematic() {
        final double absDeltaPitch = Math.abs(deltaPitch), absDeltaYaw = Math.abs(deltaYaw);
        final double accelPitch = Math.abs(absDeltaPitch - lastDeltaPitch), accelYaw = Math.abs(absDeltaYaw - lastDeltaYaw);
        final double joltPitch = Math.abs(accelPitch - deltaPitch), joltYaw = Math.abs(accelYaw - deltaYaw);

        // Cinematic method one.
        cinematic_method_1:
        {
            cinematic = (ticksSinceHigh > 20) || (ticksSinceSmooth < 8);

            if (joltYaw > 1.0 && joltPitch > 1.0) ticksSinceHigh = 0;

            if (deltaPitch > 0.0 && deltaYaw > 0.0) {
                yawSamples.add(deltaYaw);
                pitchSamples.add(deltaPitch);
            }

            // Sample sizes are always the same.
            if (yawSamples.size() != 20) break cinematic_method_1;

            // Get the cerberus/positive graph of the sample-lists
            final GraphUtil.GraphResult resultsYaw = GraphUtil.getGraph(yawSamples);
            final GraphUtil.GraphResult resultsPitch = GraphUtil.getGraph(pitchSamples);

            final int negativesPitch = resultsPitch.negatives(), negativesYaw = resultsYaw.negatives();
            final int positivesPitch = resultsPitch.positives(), positivesYaw = resultsYaw.positives();

            // Cinematic camera usually does this on *most* speeds and is accurate for the most part.
            if (positivesYaw > negativesYaw || positivesPitch > negativesPitch) ticksSinceSmooth = 0;


            yawSamples.clear();
            pitchSamples.clear();
        }

        // Cinematic method two.
        cinematic_method_2:
        {
            //Fixes exploits
            if (deltaPitch == 0F || deltaYaw == 0F) break cinematic_method_2;

            final float yawAccel = Math.abs(deltaYaw - this.lastDeltaYaw);
            final float pitchAccel = Math.abs(deltaPitch - this.lastDeltaPitch);

            final boolean invalid = MathUtil.isExponentiallySmall(yawAccel) || yawAccel == 0F
                    || MathUtil.isExponentiallySmall(pitchAccel) || pitchAccel == 0F;

            /*
             * Grab the GCD for the player on both the pitch and yaw. Thanks to our utility only taking
             * in longs we are going to expand the rotations before giving them as a parameter.
             */
            final double constantYaw = MathUtil.getGCD((long) (deltaYaw * MathUtil.EXPANDER), (long) (lastDeltaYaw * MathUtil.EXPANDER));
            final double constantPitch = MathUtil.getGCD((long) (deltaPitch * MathUtil.EXPANDER), (long) (lastDeltaPitch * MathUtil.EXPANDER));

            final boolean cinematic = !invalid && yawAccel < 1F && pitchAccel < 1F;

            if (cinematic) {
                if (constantYaw < CINEMATIC_CONSTANT && constantPitch < CINEMATIC_CONSTANT) cinematicTicks++;
            } else cinematicTicks -= cinematicTicks > 0 ? 1 : 0;

            this.cinematicTicks -= this.cinematicTicks > 5 ? 1 : 0;

            cinematic2 = (this.cinematicTicks > 2 || internalTicksSinceCinematic < 80);
            if (cinematic2 && this.cinematicTicks > 3) internalTicksSinceCinematic = 0;
        }

        if (cinematic || cinematic2) ticksSinceCinematic = 0;
    }
}
