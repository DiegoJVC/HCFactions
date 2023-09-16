package com.cobelpvp.hcfactions.commands;

import com.cobelpvp.hcfactions.persist.RedisSaveTask;
import org.bukkit.command.CommandSender;

import com.cobelpvp.atheneum.command.Command;

public class SaveRedisCommand {

    @Command(names = {"SaveRedis", "Save"}, permission = "op")
    public static void saveRedis(CommandSender sender) {
        RedisSaveTask.save(sender, false);
    }

    @Command(names = {"SaveRedis ForceAll", "Save ForceAll"}, permission = "op")
    public static void saveRedisForceAll(CommandSender sender) {
        RedisSaveTask.save(sender, true);
    }

}