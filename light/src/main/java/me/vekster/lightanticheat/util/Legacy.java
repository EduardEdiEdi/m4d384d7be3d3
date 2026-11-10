package me.vekster.lightanticheat.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

/**
 * Constants whose names changed between server versions, looked up by any of their names at runtime so the module
 * loads on old and new servers alike (a direct reference to a removed field fails the whole class).
 */
public final class Legacy {

    public static final Enchantment LUCK = get(Enchantment.class, "LUCK", "LUCK_OF_THE_SEA");
    public static final Enchantment PUNCH = get(Enchantment.class, "ARROW_KNOCKBACK", "PUNCH");
    public static final Enchantment KNOCKBACK = get(Enchantment.class, "KNOCKBACK");
    public static final PotionEffectType HASTE = get(PotionEffectType.class, "FAST_DIGGING", "HASTE");
    public static final EntityType TNT = get(EntityType.class, "PRIMED_TNT", "TNT");
    public static final Material GRASS = get(Material.class, "GRASS", "SHORT_GRASS");

    private Legacy() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type, String... names) {
        for (String n : names) {
            try {
                Object o = type.getField(n).get(null);
                if (type.isInstance(o)) return (T) o;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            }
        }
        return null;
    }
}
