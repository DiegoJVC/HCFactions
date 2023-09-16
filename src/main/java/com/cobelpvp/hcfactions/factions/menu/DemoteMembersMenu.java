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
public class DemoteMembersMenu extends Menu {

    @NonNull
    @Getter
    Faction faction;

    @Override
    public String getTitle(Player player) {
        return "Demote captains/co-leaders";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        for (UUID uuid : faction.getColeaders()) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, faction, false));
            index++;
        }

        for (UUID uuid : faction.getCaptains()) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, faction, false));
            index++;
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}