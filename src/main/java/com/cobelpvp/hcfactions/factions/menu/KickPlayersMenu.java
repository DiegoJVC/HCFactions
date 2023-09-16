package com.cobelpvp.hcfactions.factions.menu;

import com.cobelpvp.hcfactions.factions.menu.button.KickPlayerButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.atheneum.util.UUIDUtils;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class KickPlayersMenu extends Menu {

    @NonNull
    @Getter
    Faction faction;

    @Override
    public String getTitle(Player player) {
        return "Players in " + faction.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.addAll(faction.getMembers());

        Collections.sort(uuids, (u1, u2) -> UUIDUtils.name(u1).toLowerCase().compareTo(UUIDUtils.name(u2).toLowerCase()));

        for (UUID uuid : uuids) {
            buttons.put(index, new KickPlayerButton(uuid, faction));
            index++;
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
