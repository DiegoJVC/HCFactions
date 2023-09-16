package com.cobelpvp.hcfactions.commands;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.uuid.TeamsUUIDCache;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.cobelpvp.atheneum.command.Command;

public class CSVExportCommand {

    @Command(names={ "csvexport" }, permission="op")
    public static void csvExport(Player sender) {
        try (FileWriter fileWriter = new FileWriter("export.csv")) {
            fileWriter.append("Name,HasTeam,TeamBalance,TeamSize,CoalMined,DiamondMined,EmeraldMined,FishingKitUses,FriendLives,GoldMined,IronMined,Kills,LapisMined,Playtime,RedstoneMined,SoulboundLives,Balance,Whitelisted,OP").append('\n');

            for (UUID player : HCFactions.getInstance().getFirstJoinMap().getAllPlayers()) {
                Faction playerFaction = HCFactions.getInstance().getFactionHandler().getTeam(player);
                OfflinePlayer offlinePlayer = HCFactions.getInstance().getServer().getOfflinePlayer(player);

                fileWriter.append(TeamsUUIDCache.name(player)).append(",");
                fileWriter.append(playerFaction != null ? "1" : "0").append(",");
                fileWriter.append(String.valueOf(playerFaction == null ? 0 : playerFaction.getBalance())).append(",");
                fileWriter.append(String.valueOf(playerFaction == null ? 0 : playerFaction.getSize())).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getCoalMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getDiamondMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getEmeraldMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getLivesMap().getLives(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getGoldMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getIronMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getKillsMap().getKills(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getLapisMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getPlaytimeMap().getPlaytime(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getRedstoneMinedMap().getMined(player))).append(",");
                fileWriter.append(String.valueOf(HCFactions.getInstance().getWrappedBalanceMap().getBalance(player))).append(",");
                fileWriter.append(String.valueOf(offlinePlayer.isWhitelisted() ? "1" : "0")).append(",");
                fileWriter.append(String.valueOf(offlinePlayer.isOp() ? "1" : "0")).append('\n');

                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage("Done!");
    }

}