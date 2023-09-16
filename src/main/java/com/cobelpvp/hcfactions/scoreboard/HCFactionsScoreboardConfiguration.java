package com.cobelpvp.hcfactions.scoreboard;

import com.cobelpvp.atheneum.util.ColorText;
import com.cobelpvp.atheneum.scoreboard.ScoreboardConfiguration;
import com.cobelpvp.atheneum.scoreboard.TitleGetter;

public class HCFactionsScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(new TitleGetter(ColorText.translate("&6&lCobelPvP &a&l[Map 1]")));
        configuration.setScoreGetter(new HCFactionsScoreGetter());

        return (configuration);
    }

}