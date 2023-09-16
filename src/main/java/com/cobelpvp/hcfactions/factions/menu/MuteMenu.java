package com.cobelpvp.hcfactions.factions.menu;

import com.cobelpvp.hcfactions.factions.menu.button.MuteButton;
import lombok.AllArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.hcfactions.factions.Faction;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class MuteMenu extends Menu {
    private Faction faction;


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 1) {
                buttons.put(i, new MuteButton(5, faction));

            } else if (i == 3) {
                buttons.put(i, new MuteButton(15, faction));

            } else if (i == 5) {
                buttons.put(i, new MuteButton(30, faction));

            } else if (i == 7) {
                buttons.put(i, new MuteButton(60, faction));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Mute " + faction.getName();
    }
}
