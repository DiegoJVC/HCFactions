package com.cobelpvp.hcfactions.events.systemfactions.citadel.tasks;

import com.cobelpvp.hcfactions.HCFactions;
import org.bukkit.scheduler.BukkitRunnable;

public class CitadelSaveTask extends BukkitRunnable {

    public void run() {
        HCFactions.getInstance().getCitadelHandler().saveCitadelInfo();
    }

}