package com.cobelpvp.hcfactions.events.systemfactions.koth;

import com.cobelpvp.hcfactions.events.listeners.EventActivatedEvent;
import com.cobelpvp.hcfactions.events.listeners.EventCapturedEvent;
import com.cobelpvp.hcfactions.events.listeners.EventDeactivatedEvent;
import com.cobelpvp.hcfactions.events.systemfactions.koth.events.EventControlTickEvent;
import com.cobelpvp.hcfactions.events.systemfactions.koth.events.KOTHControlLostEvent;
import lombok.Getter;
import lombok.Setter;
import com.cobelpvp.atheneum.scoreboard.ScoreFunction;
import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.events.Event;
import com.cobelpvp.hcfactions.events.EventType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KOTH implements Event {

    @Getter private String name;
    @Getter private BlockVector capLocation;
    @Getter private String world;
    @Getter private int capDistance;
    @Getter private int capTime;
    @Getter private boolean hidden = false;
    @Getter @Setter boolean active;

    @Getter private transient String currentCapper;
    @Getter private transient int remainingCapTime;
    @Getter @Setter private transient boolean terminate;

    @Getter public boolean koth = true;

    @Getter private EventType type = EventType.KOTH;

    public KOTH(String name, Location location) {
        this.name = name;
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        this.capDistance = 3;
        this.capTime = 60 * 15;
        this.terminate = false;

        HCFactions.getInstance().getEventHandler().getEvents().add(this);
        HCFactions.getInstance().getEventHandler().saveEvents();
    }

    public void setLocation(Location location) {
        this.capLocation = location.toVector().toBlockVector();
        this.world = location.getWorld().getName();
        HCFactions.getInstance().getEventHandler().saveEvents();
    }

    public void setCapDistance(int capDistance) {
        this.capDistance = capDistance;
        HCFactions.getInstance().getEventHandler().saveEvents();
    }

    public void setCapTime(int capTime) {
        int oldCapTime = this.capTime;
        this.capTime = capTime;

        if (this.remainingCapTime > capTime) {
            this.remainingCapTime = capTime;
        } else if (remainingCapTime == oldCapTime) {
            this.remainingCapTime = capTime;
        }

        HCFactions.getInstance().getEventHandler().saveEvents();
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        HCFactions.getInstance().getEventHandler().saveEvents();
    }

    public boolean activate() {
        if (active) {
            return (false);
        }

        HCFactions.getInstance().getServer().getPluginManager().callEvent(new EventActivatedEvent(this));

        this.active = true;
        this.currentCapper = null;
        this.remainingCapTime = this.capTime;
        this.terminate = false;

        return (true);
    }

    public boolean deactivate() {
        if (!active) {
            return (false);
        }

        HCFactions.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));

        this.active = false;
        this.currentCapper = null;
        this.remainingCapTime = this.capTime;
        this.terminate = false;

        return (true);
    }

    public void startCapping(Player player) {
        if (currentCapper != null) {
            resetCapTime();
        }

        this.currentCapper = player.getName();
        this.remainingCapTime = capTime;
    }

    public boolean finishCapping() {
        Player capper = HCFactions.getInstance().getServer().getPlayerExact(currentCapper);

        if (capper == null) {
            resetCapTime();
            return (false);
        }

        EventCapturedEvent event = new EventCapturedEvent(this, capper);
        HCFactions.getInstance().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            resetCapTime();
            return (false);
        }

        deactivate();
        return (true);
    }

    public void resetCapTime() {
        HCFactions.getInstance().getServer().getPluginManager().callEvent(new KOTHControlLostEvent(this));

        this.currentCapper = null;
        this.remainingCapTime = capTime;

        if (terminate) {
            deactivate();
            HCFactions.getInstance().getServer().broadcastMessage(ColorText.translate("&8[&3&lKOTH&8] &3" + getName() + " &9has been terminated."));
        }
    }

    @Override
    public void tick() {
        if (currentCapper != null) {
            Player capper = HCFactions.getInstance().getServer().getPlayerExact(currentCapper);

            if (capper == null || !onCap(capper.getLocation()) || capper.isDead() || capper.getGameMode() != GameMode.SURVIVAL || capper.hasMetadata("invisible")) {
                resetCapTime();
            } else {
                if (remainingCapTime % 60 == 0 && remainingCapTime > 1 && !isHidden()) {
                    Bukkit.broadcastMessage(ColorText.translate("&8[&3&lKOTH&8] &3Someone &9is controlling &3" + getName() + " &c(" + ScoreFunction.TIME_SIMPLE.apply((float) getRemainingCapTime()) + ")"));
                }

                if (remainingCapTime <= 0) {
                    finishCapping();
                } else {
                    HCFactions.getInstance().getServer().getPluginManager().callEvent(new EventControlTickEvent(this));
                }

                this.remainingCapTime--;
            }
        } else {
            List<Player> onCap = new ArrayList<>();

            for (Player player : HCFactions.getInstance().getServer().getOnlinePlayers()) {
                if (onCap(player.getLocation()) && !player.isDead() && player.getGameMode() == GameMode.SURVIVAL && !player.hasMetadata("invisible") && !HCFactions.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                    onCap.add(player);
                }
            }

            Collections.shuffle(onCap);

            if (onCap.size() != 0) {
                startCapping(onCap.get(0));
                Player player = Bukkit.getPlayer(getCurrentCapper());
                player.sendMessage(ColorText.translate("&3* &9Entering capzone of &3" + getName()));
            }
        }
    }

    public boolean onCap(Location location) {
        if (!location.getWorld().getName().equalsIgnoreCase(world)) {
            return (false);
        }

        int xDistance = Math.abs(location.getBlockX() - capLocation.getBlockX());
        int yDistance = Math.abs(location.getBlockY() - capLocation.getBlockY());
        int zDistance = Math.abs(location.getBlockZ() - capLocation.getBlockZ());

        return xDistance <= capDistance && yDistance <= 5 && zDistance <= capDistance;
    }

}