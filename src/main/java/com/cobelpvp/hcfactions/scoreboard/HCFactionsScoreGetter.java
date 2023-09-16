package com.cobelpvp.hcfactions.scoreboard;

import java.util.Iterator;
import java.util.Map;

import com.cobelpvp.engine.moderator.profile.ModeratorProfile;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.commands.CustomTimerCreateCommand;
import com.cobelpvp.hcfactions.events.eotw.EOTW;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import com.cobelpvp.hcfactions.events.systemfactions.destroythecore.DTC;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.hcfactions.factions.commands.faction.CmdStuck;
import com.cobelpvp.hcfactions.server.EnderpearlCooldownHandler;
import com.cobelpvp.hcfactions.server.ServerHandler;
import com.cobelpvp.hcfactions.server.SpawnTagHandler;
import com.cobelpvp.hcfactions.util.Logout;
import com.cobelpvp.hcfactions.listener.GoldenAppleListener;
import com.cobelpvp.hcfactions.pvpclass.mainclasses.ArcherClass;
import com.cobelpvp.hcfactions.pvpclass.mainclasses.BardClass;
import com.cobelpvp.atheneum.autoreboot.AutoRebootHandler;
import com.cobelpvp.atheneum.scoreboard.ScoreFunction;
import com.cobelpvp.atheneum.scoreboard.ScoreGetter;
import com.cobelpvp.atheneum.util.LinkedList;
import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HCFactionsScoreGetter implements ScoreGetter {

    public void getScores(LinkedList<String> scores, Player player) {
        String spawnTagScore = getSpawnTagScore(player);
        String enderpearlScore = getEnderpearlScore(player);
        String pvpTimerScore = getPvPTimerScore(player);
        String archerMarkScore = getArcherMarkScore(player);
        String bardEffectScore = getBardEffectScore(player);
        String bardEnergyScore = getBardEnergyScore(player);
        String fstuckScore = getFStuckScore(player);
        String logoutScore = getLogoutScore(player);
        String appleScore = getAppleScore(player);
        ModeratorProfile suiteProfile = ModeratorProfile.getPlayerByName(player.getName());

        if (suiteProfile.isStaffModeEnabled()) {
            scores.add("&3&lStaff Mode");
            scores.add(" &9Vanished: " + (suiteProfile.isVanishEnabled() ? "&aTrue" : "&cFalse"));
        }

        if (spawnTagScore != null) {
            scores.add("&c&lPvP Tag: &c" + spawnTagScore);
        }

        if (HCFactions.getInstance().getOppleMap().isOnCooldown(player.getUniqueId())) {
            long millisLeft = HCFactions.getInstance().getOppleMap().getCooldown(player.getUniqueId()) - System.currentTimeMillis();
            scores.add("&6&lGopple: &c" + TimeUtils.formatIntoMMSS((int) millisLeft / 1000));
        }

        if (appleScore != null) {
            scores.add("&6Apple: **&6" + appleScore);
        }

        if (enderpearlScore != null) {
            scores.add("&3&lEnderpearl: &c" + enderpearlScore);
        }

        if (pvpTimerScore != null) {
            if (HCFactions.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
                scores.add("&a&lStarting Timer: &c" + pvpTimerScore);
            } else {
                scores.add("&a&lPvP Timer: &c" + pvpTimerScore);
            }
        }

        Iterator<Map.Entry<String, Long>> iterator = CustomTimerCreateCommand.getCustomTimers().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> timer = iterator.next();
            if (timer.getValue() < System.currentTimeMillis()) {
                iterator.remove();
                continue;
            }

            else {
                scores.add(ChatColor.translateAlternateColorCodes('&', timer.getKey()) + ": &c" + getTimerScore(timer));
            }
        }

        for (Event event : HCFactions.getInstance().getEventHandler().getEvents()) {
            if (!event.isActive() || event.isHidden()) {
                continue;
            }

            String displayName;

            switch (event.getName()) {
            case "EOTW":
                displayName = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                break;
            case "Citadel":
                displayName = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                break;
            default:
                displayName = ChatColor.BLUE.toString() + ChatColor.BOLD + event.getName();
                break;
            }

            if (event.getType() == EventType.DTC) {
                scores.add(displayName + ": &c" + ((DTC) event).getCurrentPoints());
            } else {
                scores.add(displayName + ": &c" + ScoreFunction.TIME_SIMPLE.apply((float) ((KOTH) event).getRemainingCapTime()));
            }
        }

        if (EOTW.isFfaEnabled()) {
            long ffaEnabledAt = EOTW.getFfaActiveAt();
            if (System.currentTimeMillis() < ffaEnabledAt) {
                long difference = ffaEnabledAt - System.currentTimeMillis();
                scores.add("&4&lFFA: &c" + ScoreFunction.TIME_SIMPLE.apply(difference / 1000F));
            }
        }

        if (archerMarkScore != null) {
            scores.add("&6Archer Mark: &c" + archerMarkScore);
        }

        if (bardEffectScore != null) {
            scores.add("&aBard Effect: &c" + bardEffectScore);
        }

        if (bardEnergyScore != null) {
            scores.add("&bBard Energy: &c" + bardEnergyScore);
        }

        if (fstuckScore != null) {
            scores.add("&4&lStuck: &c" + fstuckScore);
        }

        if (logoutScore != null) {
            scores.add("&4&lLogout: &c" + logoutScore);
        }

        if (AutoRebootHandler.isRebooting()) {
            scores.add("&4&lRebooting: &4" + TimeUtils.formatIntoMMSS(AutoRebootHandler.getRebootSecondsRemaining()));
        }
    }

    public String getAppleScore(Player player) {
        if (GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId()) && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()) {
            float diff = GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getHomeScore(Player player) {
        if (ServerHandler.getHomeTimer().containsKey(player.getName()) && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getFStuckScore(Player player) {
        if (CmdStuck.getWarping().containsKey(player.getName())) {
            float diff = CmdStuck.getWarping().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getLogoutScore(Player player) {
        Logout logout = ServerHandler.getTasks().get(player.getName());

        if (logout != null) {
            float diff = logout.getLogoutTime() - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return null;
    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getEnderpearlScore(Player player) {
        if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getPvPTimerScore(Player player) {
        if (HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            int secondsRemaining = HCFactions.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId());

            if (secondsRemaining >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply((float) secondsRemaining));
            }
        }

        return (null);
    }

    public String getTimerScore(Map.Entry<String, Long> timer) {
        long diff = timer.getValue() - System.currentTimeMillis();

        if (diff > 0) {
            return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
        } else {
            return (null);
        }
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEffectScore(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }

}