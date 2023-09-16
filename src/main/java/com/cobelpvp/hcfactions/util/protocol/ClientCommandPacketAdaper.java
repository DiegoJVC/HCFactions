package com.cobelpvp.hcfactions.util.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class ClientCommandPacketAdaper extends PacketAdapter {

    public ClientCommandPacketAdaper() {
        super(HCFactions.getInstance(), PacketType.Play.Client.CLIENT_COMMAND);
    }

    @Override
    public void onPacketReceiving(final PacketEvent event) {
        if (event.getPacket().getClientCommands().read(0) == EnumWrappers.ClientCommand.PERFORM_RESPAWN) {
            if (!HCFactions.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) {
                return;
            }

            long unbannedOn = HCFactions.getInstance().getDeathbanMap().getDeathban(event.getPlayer().getUniqueId());
            long left = unbannedOn - System.currentTimeMillis();
            final String time = TimeUtils.formatIntoDetailedString((int) left / 1000);
            event.setCancelled(true);

            new BukkitRunnable() {

                public void run() {
                    event.getPlayer().setMetadata("loggedout", new FixedMetadataValue(HCFactions.getInstance(), true));

                    if (HCFactions.getInstance().getServerHandler().isPreEOTW()) {
                        event.getPlayer().kickPlayer(ChatColor.GREEN + "Come back another day for SOTW!");
                    } else {
                        if (HCFactions.getInstance().getLivesMap().getLives(event.getPlayer().getUniqueId()) <= 0) {
                            event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
                        } else {
                            event.getPlayer().kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!\n" + ChatColor.YELLOW + "You have " + HCFactions.getInstance().getLivesMap().getLives(event.getPlayer().getUniqueId()) + " lives.");
                            return;
                        }
                    }
                }

            }.runTask(HCFactions.getInstance());
        }
    }

}