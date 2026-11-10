package better.anticheat.core.check;

import better.anticheat.core.BetterAnticheat;
import better.anticheat.core.configuration.ConfigSection;
import better.anticheat.core.player.Player;
import better.anticheat.core.player.Violation;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import lombok.Getter;
import lombok.Setter;
import org.apache.fory.ThreadSafeFory;
import wtf.spare.sparej.fastlist.FastObjectArrayList;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the basis for a check in the anticheat. There are a few core components to understand:
 * 1. To use the constructor with only BetterAnticheat, you must use the @CheckInfo annotation on the class.
 * 2. The "vl" is the amount of violations a player has on this check. This will automatically increase when flagged.
 * 3. You can listen to packets coming from the client through handleReceivePlayPacket.
 * 4. You can listen to packets coming from the server through handleSendPlayPacket.
 * 5. The fail method indicates that a player is cheating.
 * 6. If you override the config handling, you must super.load()!
 */
public abstract class Check implements Cloneable {

    protected BetterAnticheat plugin;
    protected Check reference;
    @Getter
    protected Player player;

    @Getter
    @Setter
    private String name, category, config;
    @Getter
    private final boolean experimental;

    @Getter
    @Setter
    private boolean enabled = false;
    /** The violation level from which a fail is reported to the host (lower = only counted). */
    private int alertVL = 10;
    private int decay = -1;

    @Getter
    private int vl = 0;
    private long lastReportMS = 0;


    /**
     * Construct the check via info provided in CheckInfo annotation.
     * This is the recommended approach but requires a @CheckInfo annotation on implementations.
     */
    public Check() {
        CheckInfo info = this.getClass().getAnnotation(CheckInfo.class);
        if (info == null) throw new InvalidParameterException("No CheckInfo annotation on class: " + this.getClass().getName() + "!");

        // Copy values from annotation.
        name = info.name();
        category = info.category();
        config = info.config();
        experimental = info.experimental();
    }

    /**
     * Construct the check via info provided in CheckInfo annotation.
     * This is the recommended approach but requires a @CheckInfo annotation on implementations.
     */
    public Check(BetterAnticheat plugin) {
        this.plugin = plugin;
        CheckInfo info = this.getClass().getAnnotation(CheckInfo.class);
        if (info == null) throw new InvalidParameterException("No CheckInfo annotation on class: " + this.getClass().getName() + "!");

        // Copy values from annotation.
        name = info.name();
        category = info.category();
        config = info.config();
        experimental = info.experimental();
    }

    /**
     * Construct the check via parameters.
     */
    public Check(BetterAnticheat plugin, String name, String category, String config, boolean experimental) {
        this.plugin = plugin;
        this.name = name;
        this.category = category;
        this.config = config;
        this.experimental = experimental;
    }

    public void handleReceivePlayPacket(PacketPlayReceiveEvent event) {
    }

    public void handleSendPlayPacket(PacketPlaySendEvent event) {
    }

    /*
     * Check set up.
     */

    public void load() {
        if (reference == null) return;
        enabled = reference.enabled;
        alertVL = reference.alertVL;
        vl = reference.vl;
    }

    public synchronized Check initialCopy(final Player player, final ThreadSafeFory copier) {
        final var plugin = this.plugin;
        this.plugin = null;

        final var check = copier.copy(this);

        this.plugin = plugin;
        check.plugin = plugin;

        check.reference = this;
        check.player = player;
        check.load();
        return check;
    }

    @Override
    protected Check clone() {
        try {
            return (Check) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /*
     * Functions.
     */

    /**
     * Handles failing the check for the player with an empty debug.
     */
    protected void fail() {
        fail(null);
    }

    /**
     * Handles failing the check for the player with a debug.
     */
    protected void fail(Object debug) {
        fail(debug, false);
    }

    /**
     * Handles failing the check for the player with a debug.
     */
    protected void fail(Object debug, final boolean verboseOnly) {
        final long currentMS = System.currentTimeMillis();
        int newVl = 0;

        player.getViolations().add(new Violation(this, new int[0], currentMS, 1));

        if (decay > 0) {
            final long minCreationTime = currentMS - decay;
            for (final var it = player.getViolations().iterator(); it.hasNext(); ) {
                final var v = it.next();
                if (v.getCreationTime() < minCreationTime && v.getCheck() == this) {
                    it.remove();
                    continue;
                }
                if (v.getCheck() == this) newVl += v.getVl();
            }
        } else {
            for (final var violation : player.getViolations()) {
                if (violation.getCheck() == this) newVl += violation.getVl();
            }
        }

        vl = Math.min(10000, newVl);

        /*
         * Modified: no chat alerts, no combat mitigation and no punishments here. A fail at or above the report VL is
         * handed to the host plugin (at most one report per check every 250 ms); the host decides what it means.
         */
        if (verboseOnly || alertVL == -1 || vl < alertVL || currentMS - lastReportMS < 250) return;
        lastReportMS = currentMS;
        plugin.getDataBridge().report(player.getUser(), category, name, vl, debug == null ? null : debug.toString());
    }

    /**
     *
     */
    public boolean load(ConfigSection section) {
        if (section == null) {
            enabled = false;
            return false;
        }

        boolean modified = false;

        // Fetch enabled status.
        if (!section.hasNode("enabled")) {
            section.setObject(Boolean.class, "enabled", true);
            modified = true;
        }
        enabled = section.getObject(Boolean.class, "enabled", true);

        // No use in wasting more time loading.
        if (!enabled) return modified;

        // Fetch alertvl.
        if (!section.hasNode("alert-vl")) {
            section.setObject(Integer.class, "alert-vl", 1);
            modified = true;
        }
        alertVL = section.getObject(Integer.class, "alert-vl", 5);

        if (!section.hasNode("decay")) {
            section.setObject(Integer.class, "decay", 1200000);
            modified = true;
        }
        decay = section.getObject(Integer.class, "decay", 1200000);

        // Remove old sections.
        for (final var old : new String[]{"punishments", "punishment-groups", "verbose-vl", "combat-mitigation-ticks-on-alert", "combat-mitigation-ticks-on-verbose"}) {
            if (section.hasNode(old)) {
                section.removeNode(old);
                modified = true;
            }
        }

        return modified;
    }
}