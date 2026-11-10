package kestrel.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired on the main thread for every detection. This plugin never alerts, kicks or bans; listeners decide.
 *
 * <ul>
 *   <li>module: "combat" (aim heuristics, interaction order, chat and client tricks) or
 *       "interaction" (killaura, autoclicker, criticals, scaffold, fast place/break, inventory, bots)</li>
 *   <li>category: the check family, e.g. "heuristic", "combat", "interaction", "inventory"</li>
 *   <li>check: the check name, e.g. "AimStabilization", "KillAuraA"</li>
 *   <li>level: the check's current violation level (decays over time)</li>
 * </ul>
 */
public final class DetectionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String module, category, check, detail;
    private final int level;

    public DetectionEvent(Player player, String module, String category, String check, int level, String detail) {
        this.player = player;
        this.module = module;
        this.category = category;
        this.check = check;
        this.level = level;
        this.detail = detail;
    }

    public Player getPlayer() { return player; }
    public String getModule() { return module; }
    public String getCategory() { return category; }
    public String getCheck() { return check; }
    public int getLevel() { return level; }
    public String getDetail() { return detail; }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }
}
