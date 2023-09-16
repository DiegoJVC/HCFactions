package com.cobelpvp.hcfactions.factions.commands;

import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.persist.RedisSaveTask;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.claims.LandBoard;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

public class CmdTeamData {

    @Command(names={ "exportteamdata" }, permission="op")
    public static void exportTeamData(CommandSender sender, @Param(name="file") String fileName) {
        File file = new File(fileName);

        if (file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name already exists.");
            return;
        }

        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

            out.writeUTF(sender.getName());
            out.writeUTF(new Date().toString());
            out.writeInt(HCFactions.getInstance().getFactionHandler().getTeams().size());

            for (Faction faction : HCFactions.getInstance().getFactionHandler().getTeams()) {
                out.writeUTF(faction.getName());
                out.writeUTF(faction.saveString(false));
            }

            sender.sendMessage(ChatColor.GOLD + "Saved " + HCFactions.getInstance().getFactionHandler().getTeams().size() + " factions to " + ChatColor.GREEN + file.getAbsolutePath() + ChatColor.GOLD + ".");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import factions! Check console for errors.");
        }
    }

    @Command(names={ "importteamdata" }, permission="op")
    public static void importTeamData(CommandSender sender, @Param(name="file") String fileName) {
        long startTime = System.currentTimeMillis();
        File file = new File(fileName);

        if (!file.exists()) {
            sender.sendMessage(ChatColor.RED + "An export under that name does not exist.");
            return;
        }

        try {
            Atheneum.getInstance().runRedisCommand((jedis) -> {
                jedis.flushAll();
                return null;
            });

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            String author = in.readUTF();
            String created = in.readUTF();
            int teamsToRead = in.readInt();

            for (int i = 0; i < teamsToRead; i++) {
                String teamName = in.readUTF();
                String teamData = in.readUTF();

                Faction faction = new Faction(teamName);
                faction.load(teamData, true);

                HCFactions.getInstance().getFactionHandler().setupTeam(faction, true);
            }

            LandBoard.getInstance().loadFromTeams(); // to update land board shit
            HCFactions.getInstance().getFactionHandler().recachePlayerTeams();
            RedisSaveTask.save(sender, true);
            sender.sendMessage(ChatColor.GOLD + "Loaded " + teamsToRead + " factions from an export created by " + ChatColor.GREEN + author + ChatColor.GOLD + " at " + ChatColor.GREEN + created + ChatColor.GOLD + " and recached claims in " + ChatColor.GREEN.toString() +  (System.currentTimeMillis() - startTime) + "ms.");
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(ChatColor.RED + "Could not import factions! Check console for errors.");
        }
    }

}