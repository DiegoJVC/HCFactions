package com.cobelpvp.hcfactions.crates.utils;

import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.hcfactions.crates.Crate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

import java.util.ArrayList;

public class Version_Util {

	private HCFactions HCFactions;

	public Version_Util(HCFactions HCFactions) {
		this.HCFactions = HCFactions;
	}

	public ItemStack getItemInPlayersHand(Player player) {
		return player.getItemInHand();
	}

	public ItemStack getItemInPlayersOffHand(Player player) {
		return null;
	}

	public void removeItemInOffHand(Player player) {

	}

	public ItemStack getSpawnEgg(EntityType entityType, Integer amount) {
		ItemStack egg = new ItemStack(Material.MONSTER_EGG, amount);
		if (entityType != null) {
			SpawnEgg spawnEgg = new SpawnEgg(entityType);
			egg.setData(spawnEgg);
		}
		return egg;
	}

	public EntityType getEntityTypeFromItemStack(ItemStack itemStack) {
		SpawnEgg spawnEgg = (SpawnEgg) itemStack.getData();
		return spawnEgg.getSpawnedType();
	}

}
