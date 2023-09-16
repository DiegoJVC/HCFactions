package com.cobelpvp.hcfactions.util.nametag;

import com.cobelpvp.atheneum.nametag.NametagInfo;
import com.cobelpvp.atheneum.nametag.NametagProvider;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.pvpclass.mainclasses.ArcherClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HCFactionsNametagProvider extends NametagProvider {

    public HCFactionsNametagProvider() {
        super("HCFactions Provider", 5);
    }

    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        Faction viewerTeam = HCFactions.getInstance().getFactionHandler().getTeam(refreshFor);
        NametagInfo nametagInfo = null;
        if (viewerTeam != null) {
            if (viewerTeam.isMember(toRefresh.getUniqueId())) {
                nametagInfo = this.createNametag(toRefresh, ChatColor.GREEN.toString(), "");
            } else if (viewerTeam.isAlly(toRefresh.getUniqueId())) {
                nametagInfo = this.createNametag(toRefresh, Faction.ALLY_COLOR.toString(), "");
            }
        }

        if (nametagInfo == null) {
            if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && (Long)ArcherClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
                nametagInfo = this.createNametag(toRefresh, HCFactions.getInstance().getServerHandler().getArcherTagColor().toString(), "");
            } else if (viewerTeam != null && viewerTeam.getFocused() != null && viewerTeam.getFocused().equals(toRefresh.getUniqueId())) {
                nametagInfo = this.createNametag(toRefresh, ChatColor.LIGHT_PURPLE.toString(), "");
            }
        }

        if (refreshFor == toRefresh) {
            nametagInfo = this.createNametag(toRefresh, ChatColor.GREEN.toString(), "");
        }

        return nametagInfo == null ? this.createNametag(toRefresh, HCFactions.getInstance().getServerHandler().getDefaultRelationColor().toString(), "") : nametagInfo;
    }

    private NametagInfo createNametag(Player displayed, String prefix, String suffix) {
        return createNametag(prefix, suffix);
    }
}