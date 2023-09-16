package com.cobelpvp.hcfactions.factions.menu;

import com.cobelpvp.hcfactions.factions.menu.button.BooleanButton;
import lombok.AllArgsConstructor;
import com.cobelpvp.atheneum.menu.Button;
import com.cobelpvp.atheneum.menu.Menu;
import com.cobelpvp.atheneum.util.Callback;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ConfirmMenu extends Menu {

    private String title;
    private Callback<Boolean> response;


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, response));

            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, response));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return title;
    }
}
