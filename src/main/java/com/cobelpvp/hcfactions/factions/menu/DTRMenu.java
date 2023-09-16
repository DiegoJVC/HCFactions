package com.cobelpvp.hcfactions.factions.menu;

import com.cobelpvp.hcfactions.factions.menu.button.DTRButton;
import lombok.AllArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class DTRMenu extends Menu {

    Faction faction;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new DTRButton(faction, false));

            } else if (i == 5) {
                buttons.put(i, new DTRButton(faction, true));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Manage DTR";
    }
}