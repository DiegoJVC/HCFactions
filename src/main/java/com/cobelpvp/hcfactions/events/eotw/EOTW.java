package com.cobelpvp.hcfactions.events.eotw;

import java.util.concurrent.TimeUnit;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.factions.Faction;
import com.cobelpvp.hcfactions.listener.EndListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.atheneum.command.Command;

public class EOTW {

    @Getter @Setter private static boolean ffaEnabled = false;
    @Getter @Setter private static long ffaActiveAt = -1L;
    
    @Command(names={ "eotw" }, permission="hcfactions.eotw")
    public static void eotw(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.DARK_RED + "This command must be ran in creative.");
            return;
        }

        HCFactions.getInstance().getServerHandler().setEOTW(!HCFactions.getInstance().getServerHandler().isEOTW());

        EndListener.endActive = !HCFactions.getInstance().getServerHandler().isEOTW();

        if (HCFactions.getInstance().getServerHandler().isEOTW()) {
            Bukkit.getScheduler().runTaskAsynchronously(HCFactions.getInstance(), () -> {
                for (Faction team : HCFactions.getInstance().getFactionHandler().getTeams()) {
                    team.setDTR(-1);
                    team.setDTRCooldown(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3L));
                    System.out.println("Setting Faction " + team.getName() + " raidable.");
                }
            });

            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
            }

            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has commenced.");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All SafeZones are now Deathban.");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in EOTW mode.");
        }
    }

    @Command(names={ "EOTW teleport" }, permission="hcfactions.eotw", hidden = true)
    public static void eotwTpAll(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (!HCFactions.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "This command must be ran during EOTW. (/eotw)");
            return;
        }

        for (Player onlinePlayer : HCFactions.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.teleport(sender.getLocation());
        }

        sender.sendMessage(ChatColor.RED + "Players teleported.");
    }

    @Command(names={ "preeotw" }, permission="hcfactions.eotw")
    public static void preeotw(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.DARK_RED + "This command must be ran in creative.");
            return;
        }

        HCFactions.getInstance().getServerHandler().setPreEOTW(!HCFactions.getInstance().getServerHandler().isPreEOTW());

        HCFactions.getInstance().getDeathbanMap().wipeDeathbans();

        if (HCFactions.getInstance().getServerHandler().isPreEOTW()) {
            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
            }

            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + ChatColor.BOLD + "EOTW is about to commence.");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + "All deathbans are now permanent.");
            HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in Pre-EOTW mode.");
        }
    }

    @Command(names = {"eotw ffa"}, permission="hcfactions.eotw")
    public static void ffa(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.DARK_RED + "This command must be ran in creative.");
            return;
        }
 
        ConversationFactory factory = new ConversationFactory(HCFactions.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to enter FFA mode? This will start a countdown that cannot be cancelled. Type yes or no to confirm.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    ffaEnabled = true;
                    ffaActiveAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "FFA countdown initiated.");
                    
                    Bukkit.getScheduler().runTask(HCFactions.getInstance(), () -> {
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD + ChatColor.BOLD + "FFA" + ChatColor.RED + ChatColor.BOLD + " will commence in: 5:00.");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + "If you ally, you will be punished.");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                    });

                    Bukkit.getScheduler().runTaskLater(HCFactions.getInstance(), () -> {
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD + ChatColor.BOLD + "FFA" + ChatColor.RED + ChatColor.BOLD + " has now commenced!");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "Good luck and have fun!");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
                        HCFactions.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                    }, 5 * 60 * 20);
                    
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "FFA initation aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    public static boolean realFFAStarted() {
        return ffaEnabled && ffaActiveAt < System.currentTimeMillis();
    }

}