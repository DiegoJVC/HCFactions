package com.cobelpvp.hcfactions.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
@Data
public class EffectSnapshot {

	private PotionEffectType effectType;
	private int amplifier;
	private int duration;

}
