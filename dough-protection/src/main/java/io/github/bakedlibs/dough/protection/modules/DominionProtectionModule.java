package io.github.bakedlibs.dough.protection.modules;

import cn.lunadeer.dominion.api.DominionAPI;
import cn.lunadeer.dominion.api.dtos.flag.EnvFlag;
import cn.lunadeer.dominion.api.dtos.flag.PriFlagGroup;
import cn.lunadeer.dominion.api.dtos.flag.Flag;
import cn.lunadeer.dominion.api.dtos.flag.FlagGroups;
import cn.lunadeer.dominion.api.dtos.flag.Flags;
import cn.lunadeer.dominion.api.dtos.flag.PriFlag;
import io.github.bakedlibs.dough.protection.Interaction;
import io.github.bakedlibs.dough.protection.ProtectionModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import java.util.List;

public class DominionProtectionModule implements ProtectionModule {

    private final Plugin plugin;
    private DominionAPI api;

    PriFlag SF_BREAK_BLOCK;
    PriFlag SF_PLACE_BLOCK;
    PriFlag SF_INTERACT_BLOCK;
    PriFlag SF_ATTACK_PLAYER;
    PriFlag SF_ATTACK_ENTITY;

    PriFlagGroup SLIME_FUN;

    public DominionProtectionModule(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Use this method to load instances of your API or other utilites you need
     */
    @Override
    public void load() {
        try {
            api = DominionAPI.getInstance();

            SF_BREAK_BLOCK = new PriFlag("sf_break_block",
                    "SlimeFun Break Block", "Allows breaking blocks in Slimefun stuff",
                    true, true, Material.STONE_PICKAXE, "minecraft:items/item/stone_pickaxe");

            SF_PLACE_BLOCK = new PriFlag("sf_place_block",
                    "SlimeFun Place Block", "Allows placing blocks in Slimefun stuff",
                    true, true, Material.GRASS_BLOCK, "minecraft:blocks/block/grass_block_side");

            SF_INTERACT_BLOCK = new PriFlag("sf_interact_block",
                    "SlimeFun Interact Block", "Allows interacting with blocks in Slimefun stuff",
                    true, true, Material.LEVER, "minecraft:blocks/block/lever");

            SF_ATTACK_PLAYER = new PriFlag("sf_attack_player",
                    "SlimeFun Attack Player", "Allows attacking players in Slimefun stuff",
                    true, true, Material.IRON_SWORD, "minecraft:items/item/iron_sword");

            SF_ATTACK_ENTITY = new PriFlag("sf_attack_entity",
                    "SlimeFun Attack Entity", "Allows attacking entities in Slimefun stuff",
                    true, true, Material.IRON_AXE, "minecraft:items/item/iron_axe");

            Flags.registerPriFlag(null, SF_BREAK_BLOCK);
            Flags.registerPriFlag(null, SF_PLACE_BLOCK);
            Flags.registerPriFlag(null, SF_INTERACT_BLOCK);
            Flags.registerPriFlag(null, SF_ATTACK_PLAYER);
            Flags.registerPriFlag(null, SF_ATTACK_ENTITY);

            SLIME_FUN = new PriFlagGroup(
                    "slime_fun",
                    "Slime Fun Stuff",
                    "Allows or denies actions in Slimefun stuff",
                    Material.SLIME_BALL,
                    "minecraft:items/item/slime_ball",
                    List.of(SF_BREAK_BLOCK, SF_PLACE_BLOCK, SF_INTERACT_BLOCK, SF_ATTACK_PLAYER, SF_ATTACK_ENTITY));

            FlagGroups.registerPriFlagGroup(null, SLIME_FUN);

            Flags.applyChanges()
                    .thenRun(() -> plugin.getLogger().info("SlimeFun flags registered to Dominion successfully!"));

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load Dominion API. Disabling Dominion Protection Module.");
        }
    }

    /**
     * This returns the {@link Plugin} for this {@link ProtectionModule}.
     *
     * @return The associated {@link Plugin}
     */
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * This method implements the functionality of this module.
     * Use it to allow or deny an Action based on the rules of your Protection
     * {@link Plugin}
     *
     * @param p      The Player that is being queried, can be offline
     * @param l      The {@link Location} of the event that is happening
     * @param action The {@link Interaction} that is taking place.
     * @return Whether the action was allowed by your {@link Plugin}
     */
    @Override
    public boolean hasPermission(OfflinePlayer p, Location l, Interaction action) {
        if (!p.isOnline()) {
            return false;
        }
        Player player = p.getPlayer();
        if (player == null) {
            return true;
        }
        Flag flag = getDominionFlagFromInteraction(action);
        if (flag == null) {
            return true;
        }
        if (flag instanceof PriFlag) {
            PriFlag preFlag = (PriFlag) flag;
            return api.checkPrivilegeFlag(l, preFlag, player);
        } else {
            EnvFlag envFlag = (EnvFlag) flag;
            return api.checkEnvironmentFlag(l, envFlag);
        }
    }

    private @Nullable Flag getDominionFlagFromInteraction(@Nonnull Interaction action) {
        switch (action) {
            case BREAK_BLOCK:
                return SF_BREAK_BLOCK;
            case PLACE_BLOCK:
                return SF_PLACE_BLOCK;
            case INTERACT_BLOCK:
                return SF_INTERACT_BLOCK;
            case ATTACK_PLAYER:
                return SF_ATTACK_PLAYER;
            case ATTACK_ENTITY:
                return SF_ATTACK_ENTITY;
            default:
                return null;
        }
    }
}
