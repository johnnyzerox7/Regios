package net.jzx7.regios.bukkit.SpoutPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.jzx7.regios.RegiosPlugin;
import net.jzx7.regios.bukkit.entity.BukkitPlayer;
import net.jzx7.regios.messages.MsgFormat;
import net.jzx7.regiosapi.entity.RegiosPlayer;
import net.jzx7.regiosapi.regions.Region;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundManager;

public class SpoutRegion {
	
	public static HashMap<String, ArrayList<UUID>> widgetBindings = new HashMap<String, ArrayList<UUID>>();
	public static HashMap<String, String> lastSong = new HashMap<String, String>();

	public static void forceTexturePack(RegiosPlayer p, Region r){
		SpoutPlayer sp = ((SpoutPlayer) ((BukkitPlayer) p).getPlayer());
		sp.setTexturePack(r.getSpoutTexturePack());
	}
	
	public static void resetTexturePack(RegiosPlayer p){
		SpoutPlayer sp = ((SpoutPlayer) ((BukkitPlayer) p).getPlayer());
		sp.resetTexturePack();
	}

	public static void sendWelcomeMessage(RegiosPlayer p, Region r) {
		SpoutPlayer sp = ((SpoutPlayer) ((BukkitPlayer) p).getPlayer());
		String msg = MsgFormat.liveFormat(r.getSpoutWelcomeMessage(), p, r);
		if (msg.length() > 26) {
			msg = msg.substring(0, 23);
			msg += "...";
		}
		sp.sendNotification(r.getName(), msg, Material.getMaterial(r.getSpoutWelcomeMaterial()));
	}

	public static void sendLeaveMessage(RegiosPlayer p, Region r) {
		SpoutPlayer sp = ((SpoutPlayer) ((BukkitPlayer) p).getPlayer());
		String msg = MsgFormat.liveFormat(r.getSpoutLeaveMessage(), p, r);
		if (msg.length() > 26) {
			msg = msg.substring(0, 23);
			msg += "...";
		}
		sp.sendNotification(r.getName(), msg, Material.getMaterial(r.getSpoutLeaveMaterial()));
	}

	public static void attachLabel(String text, double x, double y, RegiosPlayer p) {
		GenericLabel label = new GenericLabel(text);
		SpoutPlayer sp = ((SpoutPlayer) ((BukkitPlayer) p).getPlayer());
		sp.getMainScreen().attachWidget(RegiosPlugin.regios, label);

		if (widgetBindings.containsKey(p.getName())) {
			widgetBindings.get(p.getName()).add(label.getId());
		} else {
			ArrayList<UUID> push = new ArrayList<UUID>();
			push.add(label.getId());
			widgetBindings.put(p.getName(), push);
		}
	}

	public static void removeLabel(UUID uuid, RegiosPlayer p) {
		SpoutPlayer sp = ((SpoutPlayer) ((BukkitPlayer) p).getPlayer());
		sp.getMainScreen().removeWidget(sp.getMainScreen().getWidget(uuid));
	}

	public static void wipeLabels(RegiosPlayer p) {
		if (widgetBindings.containsKey(p.getName())) {
			for (UUID uuid : widgetBindings.get(p.getName())) {
				(((SpoutPlayer) ((BukkitPlayer) p).getPlayer())).getMainScreen().removeWidget((((SpoutPlayer) ((BukkitPlayer) p).getPlayer())).getMainScreen().getWidget(uuid));
			}
		}
	}

	public static void playToPlayerMusicFromUrl(RegiosPlayer p, Region r) {
		int length = r.getCustomSoundUrl().length;
		String shuffled = "";
		if (lastSong.containsKey(p.getName())) {
			shuffled = lastSong.get(p.getName());
		}
		if (length == 1) {
			shuffled = r.getCustomSoundUrl()[0];
		} else {
			if (lastSong.containsKey(p.getName())) {
				while (lastSong.get(p.getName()).equalsIgnoreCase(shuffled)) {
					int rnd = new Random().nextInt(length) + 0;
					shuffled = r.getCustomSoundUrl()[rnd];
				}
			} else {
				shuffled = r.getCustomSoundUrl()[new Random().nextInt(length - 1)];
			}
		}
		lastSong.put(p.getName(), shuffled);
		SoundManager sm = SpoutManager.getSoundManager();
		if (shuffled.length() > 5){
			sm.playCustomMusic(RegiosPlugin.regios, ((SpoutPlayer) ((BukkitPlayer) p).getPlayer()), shuffled, true);
		} else {
			if (p.getName() == r.getOwner())
			{
				p.sendMessage("Music list is empty. Please use '/regios use-music-url " + r.getName() + " false' to disable playing music.");
			}
		}
	}

	public static void stopMusicPlaying(RegiosPlayer p, Region r) {
		if (r != null) {
			current = r;
		}
		currentPlayer = p;
		cancelMusicTask();
	}

	static Region current;
	static RegiosPlayer currentPlayer;

	private static void cancelMusicTask() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RegiosPlugin.regios, new Runnable() {

			@Override
			public void run() {
				SpoutManager.getSoundManager().stopMusic((SpoutPlayer) currentPlayer, false, 2500); // 2.5s
																									// Fadeout.
			}

		}, 20L);
	}

}
