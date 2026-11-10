package better.anticheat.core.util.entity;

import better.anticheat.core.player.tracker.impl.entity.type.EntityAttribute;
import better.anticheat.core.player.tracker.impl.entity.type.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;

/**
 * Utility class for calculating bounding box sizes and related properties for various Minecraft entities.
 * This class addresses irregularities in entity dimensions, particularly for baby entities, slimes, and others,
 * which prevent direct integration into PacketEvents. It provides methods to compute width, height, and passenger offsets.
 * Note: Due to complexities in entity attributes, some calculations depend on provided EntityData.
 */
public final class BoundingBoxSize {

    /**
     * Retrieves the width of the bounding box for the given entity data.
     * This method accounts for special cases like baby animals and delegates to getWidthMinusBaby.
     *
     * @param packetEntity the entity data containing type and attributes; must not be null
     * @return the width of the entity's bounding box as a float
     */
    public static float getWidth(EntityData packetEntity) {
        // Turtles are the only baby animal that don't follow the * 0.5 rule
        return getWidthMinusBaby(packetEntity);
    }

    /**
     * Internal helper method to calculate the width of an entity's bounding box, excluding baby scaling logic.
     * Handles various entity types with specific width values based on Minecraft entity properties.
     *
     * @param packetEntity the entity data containing type and attributes; must not be null
     * @return the width of the entity's bounding box as a float
     */
    private static float getWidthMinusBaby(EntityData packetEntity) {
        final EntityType type = packetEntity.getType();
        if (EntityTypes.AXOLOTL.equals(type)) {
            return 0.75f;
        } else if (EntityTypes.PANDA.equals(type)) {
            return 1.3f;
        } else if (EntityTypes.BAT.equals(type) || EntityTypes.PARROT.equals(type) || EntityTypes.COD.equals(type) || EntityTypes.EVOKER_FANGS.equals(type) || EntityTypes.TROPICAL_FISH.equals(type) || EntityTypes.FROG.equals(type)) {
            return 0.5f;
        } else if (EntityTypes.ARMADILLO.equals(type) || EntityTypes.BEE.equals(type) || EntityTypes.PUFFERFISH.equals(type) || EntityTypes.SALMON.equals(type) || EntityTypes.SNOW_GOLEM.equals(type) || EntityTypes.CAVE_SPIDER.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.WITHER_SKELETON.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.WITHER_SKULL.equals(type) || EntityTypes.SHULKER_BULLET.equals(type)) {
            return 0.3125f;
        } else if (EntityTypes.HOGLIN.equals(type) || EntityTypes.ZOGLIN.equals(type)) {
            return 1.3964844f;
        } else if (EntityTypes.SKELETON_HORSE.equals(type) || EntityTypes.ZOMBIE_HORSE.equals(type) || EntityTypes.HORSE.equals(type) || EntityTypes.DONKEY.equals(type) || EntityTypes.MULE.equals(type)) {
            return 1.3964844f;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT)) {
            return 1.375f;
        } else if (EntityTypes.CHICKEN.equals(type) || EntityTypes.ENDERMITE.equals(type) || EntityTypes.SILVERFISH.equals(type) || EntityTypes.VEX.equals(type) || EntityTypes.TADPOLE.equals(type)) {
            return 0.4f;
        } else if (EntityTypes.RABBIT.equals(type)) {
            return 0.4f;
        } else if (EntityTypes.CREAKING.equals(type) || EntityTypes.STRIDER.equals(type) || EntityTypes.COW.equals(type) || EntityTypes.SHEEP.equals(type) || EntityTypes.MOOSHROOM.equals(type) || EntityTypes.PIG.equals(type) || EntityTypes.LLAMA.equals(type) || EntityTypes.DOLPHIN.equals(type) || EntityTypes.WITHER.equals(type) || EntityTypes.TRADER_LLAMA.equals(type) || EntityTypes.WARDEN.equals(type) || EntityTypes.GOAT.equals(type)) {
            return 0.9f;
        } else if (EntityTypes.PHANTOM.equals(type)) {
            if (packetEntity.getAttributes().containsKey(EntityAttribute.SIZE)) {
                return 0.9f + ((float) packetEntity.getAttributes().get(EntityAttribute.SIZE)) * 0.2f;
            }

            return 1.5f;
        } else if (EntityTypes.GUARDIAN.equals(type)) {
            return 0.85f;
        } else if (EntityTypes.ELDER_GUARDIAN.equals(type)) {
            return 1.9975f;
        } else if (EntityTypes.END_CRYSTAL.equals(type)) {
            return 2f;
        } else if (EntityTypes.ENDER_DRAGON.equals(type)) {
            return 16f;
        } else if (EntityTypes.FIREBALL.equals(type)) {
            return 1f;
        } else if (EntityTypes.GHAST.equals(type)) {
            return 4f;
        } else if (EntityTypes.GIANT.equals(type)) {
            return 3.6f;
        } else if (EntityTypes.IRON_GOLEM.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.MAGMA_CUBE.equals(type)) {
            if (packetEntity.getAttributes().containsKey(EntityAttribute.SIZE)) {
                final var size = (float) packetEntity.getAttributes().get(EntityAttribute.SIZE);
                return 2.04f * (0.255f * size);
            }

            return 0.98f;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT)) {
            return 0.98f;
        } else if (EntityTypes.PLAYER.equals(type)) {
            return 0.6f;
        } else if (EntityTypes.POLAR_BEAR.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.RAVAGER.equals(type)) {
            return 1.95f;
        } else if (EntityTypes.SHULKER.equals(type)) {
            return 1f;
        } else if (EntityTypes.SLIME.equals(type)) {
            if (packetEntity.getAttributes().containsKey(EntityAttribute.SIZE)) {
                final var size = (float) packetEntity.getAttributes().get(EntityAttribute.SIZE);
                return 2.04f * (0.255f * size);
            }

            return 0.3125f;
        } else if (EntityTypes.SMALL_FIREBALL.equals(type)) {
            return 0.3125f;
        } else if (EntityTypes.SPIDER.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.SQUID.equals(type)) {
            return 0.8f;
        } else if (EntityTypes.TURTLE.equals(type)) {
            return 1.2f;
        } else if (EntityTypes.ALLAY.equals(type)) {
            return 0.35f;
        } else if (EntityTypes.SNIFFER.equals(type)) {
            return 1.9f;
        } else if (EntityTypes.CAMEL.equals(type)) {
            return 1.7f;
        } else if (EntityTypes.WIND_CHARGE.equals(type)) {
            return 0.3125f;
        } else if (EntityTypes.ARMOR_STAND.equals(type)) {
            return 0.5F;
        } else if (EntityTypes.FALLING_BLOCK.equals(type)) {
            return 0.98F;
        } else if (EntityTypes.FIREWORK_ROCKET.equals(type)) {
            return 0.25F;
        }
        return 0.6f;
    }

    /**
     * Rotates a vector around the Y-axis by the given yaw angle.
     * Used for orientation calculations in bounding box contexts.
     *
     * @param yaw the yaw angle in radians for rotation
     * @param start the initial vector to rotate; must not be null
     * @return the rotated vector as a Vector3d object
     */
    private static Vector3d yRot(float yaw, Vector3d start) {
        double cos = (float) Math.cos(yaw);
        double sin = (float) Math.sin(yaw);
        return new Vector3d(
                start.x * cos + start.z * sin,
                start.y,
                start.z * cos - start.x * sin
        );
    }

    /**
     * Retrieves the height of the bounding box for the given entity data.
     * This method accounts for special cases and delegates to getHeightMinusBaby.
     *
     * @param packetEntity the entity data containing type and attributes; must not be null
     * @return the height of the entity's bounding box as a float
     */
    public static float getHeight(EntityData packetEntity) {
        // Turtles are the only baby animal that don't follow the * 0.5 rule
        return getHeightMinusBaby(packetEntity);
    }

    /**
     * Internal helper method to calculate the height of an entity's bounding box, excluding baby scaling logic.
     * Handles various entity types with specific height values based on Minecraft entity properties.
     *
     * @param packetEntity the entity data containing type and attributes; must not be null
     * @return the height of the entity's bounding box as a float
     */
    private static float getHeightMinusBaby(EntityData packetEntity) {
        final EntityType type = packetEntity.getType();
        if (EntityTypes.ARMADILLO.equals(type)) {
            return 0.65f;
        } else if (EntityTypes.AXOLOTL.equals(type)) {
            return 0.42f;
        } else if (EntityTypes.BEE.equals(type) || EntityTypes.DOLPHIN.equals(type) || EntityTypes.ALLAY.equals(type)) {
            return 0.6f;
        } else if (EntityTypes.EVOKER_FANGS.equals(type) || EntityTypes.VEX.equals(type)) {
            return 0.8f;
        } else if (EntityTypes.SQUID.equals(type)) {
            return 0.8f;
        } else if (EntityTypes.PARROT.equals(type) || EntityTypes.BAT.equals(type) || EntityTypes.PIG.equals(type) || EntityTypes.SPIDER.equals(type)) {
            return 0.9f;
        } else if (EntityTypes.WITHER_SKULL.equals(type) || EntityTypes.SHULKER_BULLET.equals(type)) {
            return 0.3125f;
        } else if (EntityTypes.BLAZE.equals(type)) {
            return 1.8f;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT)) {
            // WHY DOES VIAVERSION OFFSET BOATS? THIS MAKES IT HARD TO SUPPORT, EVEN IF WE INTERPOLATE RIGHT.
            // I gave up and just exempted boats from the reach check and gave up with interpolation for collisions
            return 0.5625f;
        } else if (EntityTypes.CAT.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.CAVE_SPIDER.equals(type)) {
            return 0.5f;
        } else if (EntityTypes.FROG.equals(type)) {
            return 0.55f;
        } else if (EntityTypes.CHICKEN.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.HOGLIN.equals(type) || EntityTypes.ZOGLIN.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.COW.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.STRIDER.equals(type)) {
            return 1.7f;
        } else if (EntityTypes.CREEPER.equals(type)) {
            return 1.7f;
        } else if (EntityTypes.DONKEY.equals(type)) {
            return 1.5f;
        } else if (EntityTypes.ELDER_GUARDIAN.equals(type)) { // TODO: 2.35 * guardian?
            return 1.9975f;
        } else if (EntityTypes.ENDERMAN.equals(type) || EntityTypes.WARDEN.equals(type)) {
            return 2.9f;
        } else if (EntityTypes.ENDERMITE.equals(type) || EntityTypes.COD.equals(type)) {
            return 0.3f;
        } else if (EntityTypes.END_CRYSTAL.equals(type)) {
            return 2f;
        } else if (EntityTypes.ENDER_DRAGON.equals(type)) {
            return 8f;
        } else if (EntityTypes.FIREBALL.equals(type)) {
            return 1f;
        } else if (EntityTypes.FOX.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.GHAST.equals(type)) {
            return 4f;
        } else if (EntityTypes.GIANT.equals(type)) {
            return 12f;
        } else if (EntityTypes.GUARDIAN.equals(type)) {
            return 0.85f;
        } else if (EntityTypes.HORSE.equals(type)) {
            return 1.6f;
        } else if (EntityTypes.IRON_GOLEM.equals(type)) {
            return 2.7f;
        } else if (EntityTypes.CREAKING.equals(type)) {
            return 2.7f;
        } else if (EntityTypes.LLAMA.equals(type) || EntityTypes.TRADER_LLAMA.equals(type)) {
            return 1.87f;
        } else if (EntityTypes.TROPICAL_FISH.equals(type)) {
            return 0.4f;
        } else if (EntityTypes.MAGMA_CUBE.equals(type)) {
            if (packetEntity.getAttributes().containsKey(EntityAttribute.SIZE)) {
                final var size = (float) packetEntity.getAttributes().get(EntityAttribute.SIZE);
                return 0.52f * size;
            }

            return 0.7f;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT)) {
            return 0.7f;
        } else if (EntityTypes.MULE.equals(type)) {
            return 1.6f;
        } else if (EntityTypes.MOOSHROOM.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.OCELOT.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.PANDA.equals(type)) {
            return 1.25f;
        } else if (EntityTypes.PHANTOM.equals(type)) {
            if (packetEntity.getAttributes().containsKey(EntityAttribute.SIZE)) {
                final var size = (float) packetEntity.getAttributes().get(EntityAttribute.SIZE);
                return 0.5f + size * 0.1f;
            }

            return 1.8f;
        } else if (EntityTypes.PLAYER.equals(type)) {
            return 1.8f;
        } else if (EntityTypes.POLAR_BEAR.equals(type)) {
            return 1.4f;
        } else if (EntityTypes.PUFFERFISH.equals(type)) {
            return 0.7f;
        } else if (EntityTypes.RABBIT.equals(type)) {
            return 0.5f;
        } else if (EntityTypes.RAVAGER.equals(type)) {
            return 2.2f;
        } else if (EntityTypes.SALMON.equals(type)) {
            return 0.4f;
        } else if (EntityTypes.SHEEP.equals(type) || EntityTypes.GOAT.equals(type)) {
            return 1.3f;
        } else if (EntityTypes.SHULKER.equals(type)) { // Could maybe guess peek size, although seems useless
            return 2f;
        } else if (EntityTypes.SILVERFISH.equals(type)) {
            return 0.3f;
        } else if (EntityTypes.SKELETON.equals(type)) {
            return 1.99f;
        } else if (EntityTypes.SKELETON_HORSE.equals(type)) {
            return 1.6f;
        } else if (EntityTypes.SLIME.equals(type)) {
            if (packetEntity.getAttributes().containsKey(EntityAttribute.SIZE)) {
                final var size = (float) packetEntity.getAttributes().get(EntityAttribute.SIZE);
                return 0.52f * size;
            }

            return 0.3125f;
        } else if (EntityTypes.SMALL_FIREBALL.equals(type)) {
            return 0.3125f;
        } else if (EntityTypes.SNOW_GOLEM.equals(type)) {
            return 1.9f;
        } else if (EntityTypes.STRAY.equals(type)) {
            return 1.99f;
        } else if (EntityTypes.TURTLE.equals(type)) {
            return 0.4f;
        } else if (EntityTypes.WITHER.equals(type)) {
            return 3.5f;
        } else if (EntityTypes.WITHER_SKELETON.equals(type)) {
            return 2.4f;
        } else if (EntityTypes.WOLF.equals(type)) {
            return 0.85f;
        } else if (EntityTypes.ZOMBIE_HORSE.equals(type)) {
            return 1.6f;
        } else if (EntityTypes.TADPOLE.equals(type)) {
            return 0.3f;
        } else if (EntityTypes.SNIFFER.equals(type)) {
            return 1.75f;
        } else if (EntityTypes.CAMEL.equals(type)) {
            return 2.375f;
        } else if (EntityTypes.BREEZE.equals(type)) {
            return 1.77f;
        } else if (EntityTypes.BOGGED.equals(type)) {
            return 1.99f;
        } else if (EntityTypes.WIND_CHARGE.equals(type)) {
            return 0.3125f;
        } else if (EntityTypes.ARMOR_STAND.equals(type)) {
            return 1.975F;
        } else if (EntityTypes.FALLING_BLOCK.equals(type)) {
            return 0.98F;
        } else if (EntityTypes.VILLAGER.equals(type)) {
            return 1.8F;
        } else if (EntityTypes.FIREWORK_ROCKET.equals(type)) {
            return 0.25F;
        }
        return 1.95f;
    }

    /**
     * Calculates the offset for passengers riding on the entity, used for position adjustments.
     * Accounts for entity-specific riding offsets, such as for horses, boats, and others.
     *
     * @param packetEntity the entity data containing type and attributes; must not be null
     * @return the passenger riding offset as a double
     */
    public static double getPassengerRidingOffset(EntityData packetEntity) {
        final EntityType type = packetEntity.getType();
        if (EntityTypes.HORSE.equals(type))
            return (getHeight(packetEntity) * 0.75) - 0.25;


        if (EntityTypes.isTypeInstanceOf(type, EntityTypes.MINECART_ABSTRACT)) {
            return 0;
        } else if (EntityTypes.isTypeInstanceOf(type, EntityTypes.BOAT)) {
            return -0.1;
        } else if (EntityTypes.HOGLIN.equals(type) || EntityTypes.ZOGLIN.equals(type)) {
            return getHeight(packetEntity) - 0.15;
        } else if (EntityTypes.LLAMA.equals(type)) {
            return getHeight(packetEntity) * 0.67;
        } else if (EntityTypes.PIGLIN.equals(type)) {
            return getHeight(packetEntity) * 0.92;
        } else if (EntityTypes.RAVAGER.equals(type)) {
            return 2.1;
        } else if (EntityTypes.SKELETON.equals(type)) {
            return (getHeight(packetEntity) * 0.75) - 0.1875;
        } else if (EntityTypes.SPIDER.equals(type)) {
            return getHeight(packetEntity) * 0.5;
        } else if (EntityTypes.STRIDER.equals(type)) {// depends on animation position, good luck getting it exactly, this is the best you can do though
            return getHeight(packetEntity) - 0.19;
        }
        return getHeight(packetEntity) * 0.75;
    }
}