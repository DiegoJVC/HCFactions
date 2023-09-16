package com.cobelpvp.hcfactions.chat;

import com.cobelpvp.hcfactions.chat.listeners.ChatListener;
import lombok.Getter;
import com.cobelpvp.hcfactions.HCFactions;

import java.util.concurrent.atomic.AtomicInteger;

public class ChatHandler {

    @Getter
    private static AtomicInteger publicMessagesSent = new AtomicInteger();

    public ChatHandler() {
        HCFactions.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), HCFactions.getInstance());
    }
}