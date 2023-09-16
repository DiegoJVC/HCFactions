package com.cobelpvp.hcfactions.factions.menu;

import com.cobelpvp.hcfactions.factions.menu.button.ChangePromotionStatusButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class PromoteMembersMenu extends Menu {

    @NonNull
    @Getter
    Faction faction;

    @Override
    public String getTitle(Player player) {
        return "Members of " + faction.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (UUID uuid : faction.getMembers()) {
            if (!faction.isOwner(uuid) && !faction.isCoLeader(uuid)) {
                buttons.put(index, new ChangePromotionStatusButton(uuid, faction, true));
                index++;
            }
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
