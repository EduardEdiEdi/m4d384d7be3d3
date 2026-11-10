package better.anticheat.core.player.tracker.impl;

import better.anticheat.core.player.Player;
import better.anticheat.core.player.tracker.Tracker;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import wtf.spare.sparej.incrementer.IntIncrementer;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.InteractionHand;
import com.github.retrooper.packetevents.protocol.world.BlockFace;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.util.Vector3i;

import java.util.Optional;

@Getter
public class ActionTracker extends Tracker {
    public ActionTracker(Player player) {
        super(player);
    }

    private final IntIncrementer digState = new IntIncrementer(0);
    // Digging
    private boolean digging = false;
    private BlockFace diggingBlockFace = BlockFace.OTHER;
    private @Nullable Vector3i diggingPosition = null;

    // Placing
    private BlockFace placingBlockFace = BlockFace.OTHER;
    private BlockFace lastPlacingBlockFace = BlockFace.OTHER;
    private @Nullable Vector3i lastPlacePosition = null;
    private @Nullable Vector3i placePosition = null;
    private @Nullable Vector3f lastCursorPosition = null;
    private @Nullable Vector3f cursorPosition = null;
    private @Nullable InteractionHand lastPlaceHand = null;
    private @Nullable InteractionHand placeHand = null;
    private Optional<ItemStack> lastPlaceItem = Optional.empty();
    private Optional<ItemStack> placeItem = Optional.empty();
    private boolean isPlacingInsideBlock = false;

    // Sprint/Sneak
    private boolean sneaking = false, wasSneaking = false;
    private boolean sprinting = false, wasSprinting = false;

    // Ticks Since
    private final IntIncrementer ticksSinceSwing = new IntIncrementer();
    private final IntIncrementer ticksSinceAttack = new IntIncrementer();
    private final IntIncrementer ticksSinceEntityInteract = new IntIncrementer();
    private final IntIncrementer ticksSinceDigging = new IntIncrementer();
    private final IntIncrementer ticksSinceSuccessfulDig = new IntIncrementer();
    private final IntIncrementer ticksSincePlace = new IntIncrementer();
    private final IntIncrementer ticksSinceRealPlace = new IntIncrementer();
    private final IntIncrementer ticksSinceRealPlaceUnder = new IntIncrementer();

    @Override
    public void handlePacketPlayReceive(PacketPlayReceiveEvent event) {
        switch (event.getPacketType()) {
            case ANIMATION -> this.ticksSinceSwing.set(0);
            case INTERACT_ENTITY -> {
                final var wrapper = new WrapperPlayClientInteractEntity(event);

                if (wrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK) {
                    this.ticksSinceAttack.set(0);
                } else {
                    this.ticksSinceEntityInteract.set(0);
                }
            }
            case PLAYER_DIGGING -> {
                final var wrapper = new WrapperPlayClientPlayerDigging(event);
                // State
                switch (wrapper.getAction()) {
                    case FINISHED_DIGGING: {
                        if (digState.get() > 1) {
                            ticksSinceSuccessfulDig.set(0);
                        }
                    }
                    case RELEASE_USE_ITEM:
                    case CANCELLED_DIGGING: {
                        digState.set(0);
                        break;
                    }

                    case START_DIGGING: {
                        digState.increment(0);
                        break;
                    }
                }

                // Block face + pos
                diggingBlockFace = wrapper.getBlockFace();
                diggingPosition = wrapper.getBlockPosition();
                digging = digState.get() > 1;
            }
            case PLAYER_BLOCK_PLACEMENT -> {
                final var wrapper = new WrapperPlayClientPlayerBlockPlacement(event);
                // Block face
                // Update block face
                lastPlacingBlockFace = placingBlockFace;
                placingBlockFace = wrapper.getFace();

                // Update place position
                lastPlacePosition = placePosition;
                placePosition = wrapper.getBlockPosition();

                // Update cursor position
                lastCursorPosition = cursorPosition;
                cursorPosition = wrapper.getCursorPosition();

                // Update hand
                lastPlaceHand = placeHand;
                placeHand = wrapper.getHand();

                // Update inside block state
                isPlacingInsideBlock = wrapper.getInsideBlock().orElse(false);

                // Update item
                lastPlaceItem = placeItem;
                placeItem = wrapper.getItemStack();

                // Reset place tick counter
                ticksSincePlace.set(0);

                if (wrapper.getCursorPosition().getX() != 0.0 || wrapper.getCursorPosition().getY() != 0.0 || wrapper.getCursorPosition().getZ() != 0.0) {
                    if (wrapper.getBlockPosition().getY() < 2000) {
                        if (wrapper.getItemStack().isPresent()) {
                            final var itemStack = wrapper.getItemStack().get();
                            if (!itemStack.isEmpty()) {
                                ticksSinceRealPlace.set(0);

                                if (wrapper.getBlockPosition().getY() <= player.getPositionTracker().getY()) {
                                    ticksSinceRealPlaceUnder.set(0);
                                }

                                // TODO: We can confirm this is a "real" place, therefore we can maybe use this in other places.
                            }
                        }
                    }
                }
            }

            case CLIENT_TICK_END -> tick();
            case PLAYER_FLYING -> {
                // Handle no tick end versions.
                if (player.getUser().getClientVersion().isOlderThan(ClientVersion.V_1_21_2)) {
                    tick();
                }
            }

            case ENTITY_ACTION -> {
                final var wrapper = new WrapperPlayClientEntityAction(event);

                switch (wrapper.getAction()) {
                    // Sprinting
                    case START_SPRINTING: {
                        this.sprinting = true;
                        break;
                    }
                    case STOP_SPRINTING: {
                        this.sprinting = false;
                        break;
                    }
                    // Sneaking
                    case START_SNEAKING: {
                        this.sneaking = true;
                        break;
                    }
                    case STOP_SNEAKING: {
                        this.sneaking = false;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void handlePacketPlaySend(PacketPlaySendEvent event) {
    }

    private void tick() {
        // Update digging timer
        if (digging) {
            ticksSinceDigging.increment();
        } else {
            ticksSinceDigging.set(0);
        }

        // Increment tick counters
        ticksSinceSuccessfulDig.increment();
        ticksSincePlace.increment();
        ticksSinceRealPlace.increment();
        ticksSinceRealPlaceUnder.increment();

        // Update previous states
        wasSneaking = sneaking;
        wasSprinting = sprinting;

        // Combat tickets
        this.ticksSinceAttack.increment();
        this.ticksSinceSwing.increment();
        this.ticksSinceEntityInteract.increment();
    }
}
