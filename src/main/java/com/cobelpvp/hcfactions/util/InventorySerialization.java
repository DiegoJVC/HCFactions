package com.cobelpvp.hcfactions.util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.cobelpvp.atheneum.Atheneum;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.inventory.ItemStack;

public class InventorySerialization {

	private static final Type TYPE = new TypeToken<ItemStack[]>() {}.getType();

	public static BasicDBObject serialize(ItemStack[] armor, ItemStack[] inventory) {
		BasicDBList armorDBObject = serialize(armor);
		BasicDBList inventoryDBObject = serialize(inventory);

		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("ArmorContents", armorDBObject);
		dbObject.put("InventoryContents", inventoryDBObject);

		return dbObject;
	}

	public static BasicDBList serialize(ItemStack[] items) {
		List<ItemStack> kits = new ArrayList<>(Arrays.asList(items));
		kits.removeIf(Objects::isNull);
		return (BasicDBList) JSON.parse(Atheneum.PLAIN_GSON.toJson(kits.toArray(new ItemStack[kits.size()])));
	}

	public static ItemStack[] deserialize(BasicDBList dbList) {
		return Atheneum.PLAIN_GSON.fromJson(Atheneum.PLAIN_GSON.toJson(dbList), TYPE);
	}

}
