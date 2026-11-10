package me.vekster.lightanticheat;

import me.vekster.lightanticheat.check.Check;
import me.vekster.lightanticheat.check.buffer.Buffer;
import me.vekster.lightanticheat.check.checks.combat.autoclicker.AutoClickerA;
import me.vekster.lightanticheat.check.checks.combat.autoclicker.AutoClickerB;
import me.vekster.lightanticheat.check.checks.combat.criticals.CriticalsA;
import me.vekster.lightanticheat.check.checks.combat.criticals.CriticalsB;
import me.vekster.lightanticheat.check.checks.combat.killaura.*;
import me.vekster.lightanticheat.check.checks.interaction.blockbreak.BlockBreakA;
import me.vekster.lightanticheat.check.checks.interaction.fastbreak.FastBreakA;
import me.vekster.lightanticheat.check.checks.interaction.fastplace.FastPlaceA;
import me.vekster.lightanticheat.check.checks.interaction.ghostbreak.GhostBreakA;
import me.vekster.lightanticheat.check.checks.interaction.scaffold.ScaffoldA;
import me.vekster.lightanticheat.check.checks.interaction.scaffold.ScaffoldB;
import me.vekster.lightanticheat.check.checks.inventory.sorting.SortingA;
import me.vekster.lightanticheat.check.checks.inventory.swapping.ItemSwapA;
import me.vekster.lightanticheat.check.checks.packet.morepackets.MorePacketsB;
import me.vekster.lightanticheat.check.checks.player.autobot.AutoBotA;
import me.vekster.lightanticheat.check.checks.player.skinblinker.SkinBlinkerA;
import me.vekster.lightanticheat.event.LACEventCaller;
import me.vekster.lightanticheat.event.packetrecive.FlyingPacketReader;
import me.vekster.lightanticheat.listener.unloadedchunk.UnloadedChunkListener;
import me.vekster.lightanticheat.player.LACPlayerListener;
import me.vekster.lightanticheat.util.api.ApiUtil;
import me.vekster.lightanticheat.util.config.ConfigManager;
import me.vekster.lightanticheat.util.hook.server.folia.FoliaUtil;
import me.vekster.lightanticheat.util.logger.Logger;
import me.vekster.lightanticheat.util.npc.ExternalNPCUtil;
import me.vekster.lightanticheat.util.player.connectionstability.ConnectionStabilityListener;
import me.vekster.lightanticheat.util.player.cps.CPSListener;
import me.vekster.lightanticheat.util.tps.TPSCalculator;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Modified: runs as part of a host plugin (the host extends this class). No commands, update checker or metrics;
 * only the combat, interaction, inventory and bot checks are registered.
 */
public class Main extends JavaPlugin {

    private static Main instance;
    private static LACEventCaller eventCaller;
    private static final long BUFFER_DURATION_MILS = 20 * 1000L;
    private static final int PLUGIN_ID = 112053;
    private static final int STATS_ID = 12841;

    @Override
    public void onEnable() {
        instance = this;
        FoliaUtil.loadFoliaUtil();
        FlyingPacketReader.load();
        ConfigManager.loadConfig();

        Buffer.loadBufferCleaner(BUFFER_DURATION_MILS);
        TPSCalculator.loadTPSCalculator();
        Logger.logFile("");
        ApiUtil.setApiInstance();

        LACPlayerListener.loadLACPlayerListener();
        registerListener(new LACPlayerListener());

        ExternalNPCUtil.loadExternalNPCUtil();


        eventCaller = new LACEventCaller();
        registerListener(eventCaller);

        UnloadedChunkListener.handleUnloadedChunks();

        CPSListener.loadCpsCalculatorOnReload();
        CPSListener.loadCpsCalculator();
        registerListener(new CPSListener());
        ConnectionStabilityListener.loadConnectionCalculatorOnReload();
        ConnectionStabilityListener.loadConnectionCalculator();
        registerListener(new ConnectionStabilityListener());

        registerCheckListener(new KillAuraA());
        registerCheckListener(new KillAuraB());
        registerCheckListener(new KillAuraC());
        registerCheckListener(new KillAuraD());
        registerCheckListener(new CriticalsA());
        registerCheckListener(new CriticalsB());
        registerCheckListener(new AutoClickerA());
        registerCheckListener(new AutoClickerB());
        registerCheckListener(new FastPlaceA());
        registerCheckListener(new GhostBreakA());
        registerCheckListener(new FastBreakA());
        registerCheckListener(new BlockBreakA());
        registerCheckListener(new ScaffoldA());
        registerCheckListener(new ScaffoldB());
        registerCheckListener(new SortingA());
        registerCheckListener(new ItemSwapA());
        registerCheckListener(new MorePacketsB());
        registerCheckListener(new AutoBotA());
        registerCheckListener(new SkinBlinkerA());
    }

    @Override
    public void onDisable() {
        FoliaUtil.shutdown();
        if (eventCaller != null)
            eventCaller.close();
    }

    public static Main getInstance() {
        return instance;
    }

    public static long getBufferDurationMils() {
        return BUFFER_DURATION_MILS;
    }

    public static int getPluginId() {
        return PLUGIN_ID;
    }

    public static int getStatsId() {
        return STATS_ID;
    }

    private void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerCheckListener(Object object) {
        Check.registerListener(((Check) object).getCheckSetting().name, (Listener) object);
    }

}

