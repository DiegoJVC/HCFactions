package com.cobelpvp.hcfactions.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.cobelpvp.atheneum.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import lombok.Getter;
import com.cobelpvp.atheneum.command.Command;
import com.cobelpvp.atheneum.command.Param;

public class CustomTimerCreateCommand {

    @Getter private static Map<String, Long> customTimers = new HashMap<>();

    @Command(names={ "customtimer create" }, permission="hcfactions.customtimer")
    public static void customTimerCreate(CommandSender sender, @Param(name="time") int time, @Param(name="title", wildcard=true) String title) {
        if (time == 0) {
            customTimers.remove(title);
        } else {
            customTimers.put(title, System.currentTimeMillis() + (time * 1000));
        }
    }
}