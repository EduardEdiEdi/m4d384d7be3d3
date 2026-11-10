package me.vekster.lightanticheat.check;

public enum CheckName {
    KILLAURA_A(CheckType.COMBAT, "KillAura_A", "AimBot"),
    KILLAURA_B(CheckType.COMBAT, "KillAura_B", "HitBox"),
    KILLAURA_C(CheckType.COMBAT, "KillAura_C", "ThroughBlock"),
    KILLAURA_D(CheckType.COMBAT, "KillAura_D", "Impossible"),
    CRITICALS_A(CheckType.COMBAT, "Criticals_A", "Packet"),
    CRITICALS_B(CheckType.COMBAT, "Criticals_B", "MiniJump"),
    AUTOCLICKER_A(CheckType.COMBAT, "AutoClicker_A", "Pattern"),
    AUTOCLICKER_B(CheckType.COMBAT, "AutoClicker_B", "Impossible"),
    FASTPLACE_A(CheckType.INTERACTION, "FastPlace_A", "FastPlaceA"),
    GHOSTBREAK_A(CheckType.INTERACTION, "GhostBreak_A", "ThroughBlock"),
    FASTBREAK_A(CheckType.INTERACTION, "FastBreak_A", "MiningSpeed"),
    BLOCKBREAK_A(CheckType.INTERACTION, "BlockBreak_A", "Rotation"),
    SCAFFOLD_A(CheckType.INTERACTION, "Scaffold_A", "Rotation"),
    SCAFFOLD_B(CheckType.INTERACTION, "Scaffold_B", "Sprint"),
    MOREPACKETS_B(CheckType.PACKET, "MorePackets_B", "Nuker"),
    SORTING_A(CheckType.INVENTORY, "Sorting_A", "InstantSorting"),
    ITEMSWAP_A(CheckType.INVENTORY, "ItemSwap_A", "WhileWalking"),
    AUTOBOT_A(CheckType.PLAYER, "AutoBot_A", "AutoBot"),
    SKINBLINKER_A(CheckType.PLAYER, "SkinBlinker_A", "SkinBlinker");

    public enum CheckType {
        MOVEMENT,
        COMBAT,
        INTERACTION,
        PACKET,
        INVENTORY,
        PLAYER
    }

    public final CheckType type;
    public final String title;
    public final String description;
    public final String group;
    public final Character check;

    CheckName(CheckType type, String title, String description) {
        this.type = type;
        this.title = title.replace("_", "");
        this.description = description;
        this.group = title.split("_", 2)[0];
        this.check = title.split("_", 2)[1].charAt(0);
    }
}

