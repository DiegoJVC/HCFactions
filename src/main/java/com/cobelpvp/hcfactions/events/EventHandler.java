package com.cobelpvp.hcfactions.events;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.cobelpvp.hcfactions.events.systemfactions.destroythecore.DTC;
import com.cobelpvp.hcfactions.events.systemfactions.destroythecore.DTCListener;
import com.cobelpvp.hcfactions.events.systemfactions.koth.EventScheduledTime;
import com.cobelpvp.hcfactions.events.systemfactions.koth.KOTH;
import com.cobelpvp.hcfactions.events.systemfactions.koth.listeners.KOTHListener;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import com.cobelpvp.hcfactions.HCFactions;
import com.cobelpvp.atheneum.Atheneum;
import com.cobelpvp.atheneum.command.TeamsCommandHandler;
import lombok.Setter;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.DARK_GRAY;

public class EventHandler {

	@Getter private Set<Event> events = new HashSet<>();
	@Getter
	private Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();

	@Getter
	@Setter
	private boolean scheduleEnabled;

	public EventHandler() {
		loadEvents();
		loadSchedules();

		HCFactions.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), HCFactions.getInstance());
		HCFactions.getInstance().getServer().getPluginManager().registerEvents(new DTCListener(), HCFactions.getInstance());
		HCFactions.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), HCFactions.getInstance());
		TeamsCommandHandler.registerParameterType(Event.class, new EventParameterType());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Event event : events) {
					if (event.isActive()) {
						event.tick();
					}
				}
			}
		}.runTaskTimer(HCFactions.getInstance(), 5L, 20L);

		HCFactions.getInstance().getServer().getScheduler().runTaskTimer(HCFactions.getInstance(), () -> {
			activateKOTHs();
		}, 20L, 20L);
		// The initial delay of 5 ticks is to 'offset' us with the scoreboard handler.
	}

	public void loadEvents() {
		try {
			File eventsBase = new File(HCFactions.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {
				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : Objects.requireNonNull(subEventsBase.listFiles())) {
					if (eventFile.getName().endsWith(".json")) {
						events.add(Atheneum.GSON.fromJson(FileUtils.readFileToString(eventFile), eventType == EventType.KOTH ? KOTH.class : DTC.class));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// look for a previously active Event, if present deactivate and start it after 15 seconds
		events.stream().filter(Event::isActive).findFirst().ifPresent((event) -> {
			event.setActive(false);
			Bukkit.getScheduler().runTaskLater(HCFactions.getInstance(), () -> {
				// if anyone had started a Event within the last 15 seconds,
				// don't activate previously active one
				if (events.stream().noneMatch(Event::isActive)) {
					event.activate();
				}
			}, 300L);
		});
	}

	public void loadSchedules() {
		EventSchedule.clear();

		try {
			File eventSchedule = new File(HCFactions.getInstance().getDataFolder(), "eventSchedule.json");

			if (!eventSchedule.exists()) {
				eventSchedule.createNewFile();
				BasicDBObject schedule = new BasicDBObject();
				int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
				List<String> allevents = new ArrayList<>();

				for (Event event : getEvents()) {
					if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")) {
						continue;
					}

					allevents.add(event.getName());
				}

				for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
					int day = (currentDay + dayOffset) % 365;
					EventScheduledTime[] times = new EventScheduledTime[]{

							new EventScheduledTime(day, 0, 00), // 00:00am EST
							new EventScheduledTime(day, 2, 00), // 02:00am EST
							new EventScheduledTime(day, 4, 00), // 04:00am EST
							new EventScheduledTime(day, 6, 00), // 06:00am EST
							new EventScheduledTime(day, 8, 00), // 08:00am EST
							new EventScheduledTime(day, 10, 00), // 010:00am EST
							new EventScheduledTime(day, 12, 00), // 12:00pm EST
							new EventScheduledTime(day, 14, 00), // 14:00pm EST
							new EventScheduledTime(day, 16, 00), // 16:00pm EST
							new EventScheduledTime(day, 18, 00), // 18:00pm EST
							new EventScheduledTime(day, 20, 00), // 20:00pm EST
							new EventScheduledTime(day, 22, 00) // 22:00pm EST

					};

					Collections.shuffle(allevents);

					if (!allevents.isEmpty()) {
						for (int eventTimeIndex = 0; eventTimeIndex < times.length; eventTimeIndex++) {
							EventScheduledTime eventTime = times[eventTimeIndex];
							String eventName = allevents.get(eventTimeIndex % allevents.size());

							schedule.put(eventTime.toString(), eventName);
						}
					}
				}

				FileUtils.write(eventSchedule, Atheneum.GSON.toJson(new JsonParser().parse(schedule.toString())));
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(eventSchedule));

			if (dbo != null) {
				for (Map.Entry<String, Object> entry : dbo.entrySet()) {
					EventScheduledTime scheduledTime = EventScheduledTime.parse(entry.getKey());
					this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fillSchedule() {
		List<String> allevents = new ArrayList<>();

		for (Event event : getEvents()) {
			if (event.isHidden() || event.getName().equalsIgnoreCase("EOTW") || event.getName().equalsIgnoreCase("Citadel")) {
				continue;
			}

			allevents.add(event.getName());
		}

		for (int minute = 0; minute < 60; minute++) {
			for (int hour = 0; hour < 24; hour++) {
				this.EventSchedule.put(new EventScheduledTime(Calendar.getInstance().get(Calendar.DAY_OF_YEAR), (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + hour) % 24, minute), allevents.get(0));
			}
		}
	}

	public void saveEvents() {
		try {
			File eventsBase = new File(HCFactions.getInstance().getDataFolder(), "events");

			if (!eventsBase.exists()) {
				eventsBase.mkdir();
			}

			for (EventType eventType : EventType.values()) {

				File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());

				if (!subEventsBase.exists()) {
					subEventsBase.mkdir();
				}

				for (File eventFile : subEventsBase.listFiles()) {
					eventFile.delete();
				}
			}

			for (Event event : events) {
				File eventFile = new File(new File(eventsBase, event.getType().name().toLowerCase()), event.getName() + ".json");
				FileUtils.write(eventFile, Atheneum.GSON.toJson(event));
				Bukkit.getLogger().info("Writing " + event.getName() + " to " + eventFile.getAbsolutePath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Event getEvent(String name) {
		for (Event event : events) {
			if (event.getName().equalsIgnoreCase(name)) {
				return (event);
			}
		}

		return (null);
	}

	private void activateKOTHs() {
		// Don't start a KOTH during EOTW.
		if (HCFactions.getInstance().getServerHandler().isPreEOTW()) {
			return;
		}

		// Don't start a KOTH if another one is active.
		for (Event koth : HCFactions.getInstance().getEventHandler().getEvents()) {
			if (koth.isActive()) {
				return;
			}
		}

		EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());

		if (HCFactions.getInstance().getEventHandler().getEventSchedule().containsKey(scheduledTime)) {
			String resolvedName = HCFactions.getInstance().getEventHandler().getEventSchedule().get(scheduledTime);
			Event resolved = HCFactions.getInstance().getEventHandler().getEvent(resolvedName);

			if (scheduledTime.getHour() == 15 && scheduledTime.getMinutes() == 30 && resolvedName.equals("Conquest")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "conquestadmin start");
				return;
			}

			if (resolved == null) {
				HCFactions.getInstance().getLogger().warning("The event scheduler has a schedule for an event named " + resolvedName + ", but the event does not exist.");
				return;
			}

			if (Bukkit.getOnlinePlayers().size() < 5) {
				EventSchedule.remove(scheduledTime);
				Bukkit.broadcastMessage(ChatColor.RED + "A KOTH would've started however there were under 5 players online.");
				Bukkit.broadcastMessage(ChatColor.RED + "A KOTH would've started however there were under 5 players online.");

				HCFactions.getInstance().getLogger().warning("The event scheduler cannot start an event w/ under 5 players on.");
				return;
			}

			resolved.activate();
		}
	}

	private void terminateKOTHs() {
		EventScheduledTime nextScheduledTime = EventScheduledTime.parse(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)));

		if (HCFactions.getInstance().getEventHandler().getEventSchedule().containsKey(nextScheduledTime)) {
			// We have a KOTH about to start. Prepare for it.
			for (Event activeEvent : HCFactions.getInstance().getEventHandler().getEvents()) {
				if (activeEvent.getType() != EventType.KOTH) {
					continue;
				}
				KOTH activeKoth = (KOTH) activeEvent;
				if (!activeKoth.isHidden() && activeKoth.isActive() && !activeKoth.getName().equals("Citadel") && !activeKoth.getName().equals("EOTW")) {
					if (activeKoth.getCurrentCapper() != null && !activeKoth.isTerminate()) {
						activeKoth.setTerminate(true);
						HCFactions.getInstance().getServer().broadcastMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + ChatColor.BLUE + activeKoth.getName() + ChatColor.DARK_RED + " will be terminated if knocked.");
					} else {
						activeKoth.deactivate();
						HCFactions.getInstance().getServer().broadcastMessage(DARK_GRAY + "[" + DARK_AQUA + BOLD + "KOTH" + DARK_GRAY + "] " + ChatColor.BLUE + activeKoth.getName() + ChatColor.RED + " has been terminated.");
					}
				}
			}
		}
	}

	public List<Event> getEventsByType(EventType type) {
		List<Event> toReturn = new ArrayList<>();
		events.stream().filter(event -> event.getType() == type).forEach(toReturn::add);
		return toReturn;
	}

}
