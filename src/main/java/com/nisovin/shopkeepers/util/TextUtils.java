package com.nisovin.shopkeepers.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nisovin.shopkeepers.api.util.ChunkCoords;
import com.nisovin.shopkeepers.spigot.text.Text;

/**
 * Text and messaging utilities.
 * <p>
 * In contrast to {@link StringUtils}, this contains utilities that are more minecraft and messaging specific.
 */
public class TextUtils {

	private TextUtils() {
	}

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));
	static {
		DECIMAL_FORMAT.setGroupingUsed(false);
	}

	public static String getLocationString(Location location) {
		return getLocationString(location.getWorld().getName(), location.getX(), location.getY(), location.getZ());
	}

	public static String getLocationString(Block block) {
		return getLocationString(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
	}

	// more performant variant if coordinates are integers:
	public static String getLocationString(String worldName, int x, int y, int z) {
		return worldName + "," + x + "," + y + "," + z;
	}

	public static String getLocationString(String worldName, double x, double y, double z) {
		return worldName + "," + DECIMAL_FORMAT.format(x) + "," + DECIMAL_FORMAT.format(y) + "," + DECIMAL_FORMAT.format(z);
	}

	public static String getChunkString(ChunkCoords chunk) {
		return getChunkString(chunk.getWorldName(), chunk.getChunkX(), chunk.getChunkZ());
	}

	public static String getChunkString(Chunk chunk) {
		return getChunkString(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
	}

	public static String getChunkString(String worldName, int cx, int cz) {
		return worldName + "," + cx + "," + cz;
	}

	public static String getPlayerString(Player player) {
		return getPlayerString(player.getName(), player.getUniqueId());
	}

	public static String getPlayerString(String playerName, UUID playerUUID) {
		// either of them might be null
		if (playerName != null) {
			return playerName + (playerUUID == null ? "" : " (" + playerUUID.toString() + ")");
		} else if (playerUUID != null) {
			return playerUUID.toString();
		} else {
			return "[unknown]";
		}
	}

	public static String getPlayerNameOrUUID(String playerName, UUID playerUUID) {
		// either of them might be null
		// prefer name, else use uuid
		if (playerName != null) {
			return playerName;
		} else if (playerUUID != null) {
			return playerUUID.toString();
		} else {
			return "[unknown]";
		}
	}

	public static Text getPlayerText(Player player) {
		assert player != null;
		String playerName = player.getName();
		String playerUUIDString = player.getUniqueId().toString();
		return new Text(playerName).insertion(playerUUIDString).hoverEvent(playerUUIDString);
	}

	public static Text getPlayerText(String playerName, UUID playerUUID) {
		// either of them might be null
		// prefer name, else use uuid
		if (playerName != null) {
			Text text = new Text(playerName);
			if (playerUUID != null) {
				String playerUUIDString = playerUUID.toString();
				text.insertion(playerUUIDString).hoverEvent(playerUUIDString);
			}
			return text;
		} else if (playerUUID != null) {
			return new Text(playerUUID.toString());
		} else {
			return new Text("[unknown]");
		}
	}

	private static final char COLOR_CHAR_ALTERNATIVE = '&';
	private static final Pattern STRIP_COLOR_ALTERNATIVE_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR_ALTERNATIVE) + "[0-9A-FK-OR]");

	// reverse of ChatColor#translateAlternateColorCodes
	public static String translateColorCodesToAlternative(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if (b[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
				b[i] = altColorChar;
				b[i + 1] = Character.toLowerCase(b[i + 1]);
			}
		}
		return new String(b);
	}

	public static String stripColor(String colored) {
		if (colored == null) return null;
		String uncolored = ChatColor.stripColor(colored);
		uncolored = STRIP_COLOR_ALTERNATIVE_PATTERN.matcher(uncolored).replaceAll("");
		return uncolored;
	}

	public static String decolorize(String colored) {
		if (colored == null) return null;
		return translateColorCodesToAlternative(COLOR_CHAR_ALTERNATIVE, colored);
	}

	public static List<String> decolorize(List<String> colored) {
		if (colored == null) return null;
		List<String> decolored = new ArrayList<>(colored.size());
		for (String string : colored) {
			decolored.add(translateColorCodesToAlternative(COLOR_CHAR_ALTERNATIVE, string));
		}
		return decolored;
	}

	// decolorizes string entries, otherwise adopts them as they are
	public static List<Object> decolorizeUnknown(List<?> colored) {
		if (colored == null) return null;
		List<Object> decolored = new ArrayList<>(colored.size());
		for (Object entry : colored) {
			Object decolorizedEntry = entry;
			if (entry instanceof String) {
				decolorizedEntry = translateColorCodesToAlternative(COLOR_CHAR_ALTERNATIVE, (String) entry);
			}
			decolored.add(decolorizedEntry);
		}
		return decolored;
	}

	public static String colorize(String message) {
		if (message == null || message.isEmpty()) return message;
		return ChatColor.translateAlternateColorCodes(COLOR_CHAR_ALTERNATIVE, message);
	}

	public static List<String> colorize(List<String> messages) {
		if (messages == null) return messages;
		List<String> colored = new ArrayList<>(messages.size());
		for (String message : messages) {
			colored.add(colorize(message));
		}
		return colored;
	}

	// colorizes string entries, otherwise adopts them as they are
	public static List<Object> colorizeUnknown(List<?> uncolored) {
		if (uncolored == null) return null;
		List<Object> colored = new ArrayList<>(uncolored.size());
		for (Object entry : uncolored) {
			Object colorizedEntry = entry;
			if (entry instanceof String) {
				colorizedEntry = colorize((String) entry);
			}
			colored.add(colorizedEntry);
		}
		return colored;
	}

	public static String replaceArgs(String message, String... args) {
		if (!StringUtils.isEmpty(message) && args != null && args.length >= 2) {
			// replace arguments (key-value replacement):
			String key;
			String value;
			for (int i = 1; i < args.length; i += 2) {
				key = args[i - 1];
				value = args[i];
				if (key == null || value == null) continue; // skip invalid arguments
				// assumption: each key occurs at most once
				message = StringUtils.replaceFirst(message, key, value);
			}
		}
		return message;
	}

	public static String replaceArgs(String message, String[]... argSets) {
		if (!StringUtils.isEmpty(message) && argSets != null) {
			for (String[] argSet : argSets) {
				message = replaceArgs(message, argSet);
			}
		}
		return message;
	}

	public static String replaceArgs(String message, String[] argSet, String... moreArgs) {
		if (!StringUtils.isEmpty(message)) {
			message = replaceArgs(message, argSet);
			message = replaceArgs(message, moreArgs);
		}
		return message;
	}

	public static <T> String replaceArgs(String message, Iterable<Map.Entry<String, T>> args) {
		if (!StringUtils.isEmpty(message) && args != null) {
			// replace arguments (key-value replacement):
			for (Entry<String, ?> entry : args) {
				if (entry == null) continue;
				String key = entry.getKey();
				String value = String.valueOf(entry.getValue());
				if (key == null || value == null) continue; // skip invalid arguments
				// assumption: each key occurs at most once
				message = StringUtils.replaceFirst(message, key, value);
			}
		}
		return message;
	}

	public static <T> String replaceArgs(String message, Map<String, T> args) {
		return replaceArgs(message, (args == null) ? null : args.entrySet());
	}

	public static List<String> replaceArgs(Collection<String> messages, String... args) {
		List<String> replaced = new ArrayList<>(messages.size());
		for (String message : messages) {
			replaced.add(replaceArgs(message, args));
		}
		return replaced;
	}

	public static void sendMessage(CommandSender sender, String message) {
		// skip if sender is null or message is empty:
		if (sender == null || StringUtils.isEmpty(message)) return;

		// send (potentially multiline) message:
		String[] msgs = message.split("\n");
		for (String msg : msgs) {
			sender.sendMessage(msg);
		}
	}

	public static void sendMessage(CommandSender sender, String message, String... args) {
		// replace message arguments and then send:
		sendMessage(sender, replaceArgs(message, args));
	}

	public static <T> void sendMessage(CommandSender sender, String message, Iterable<Map.Entry<String, T>> args) {
		// replace message arguments and then send:
		sendMessage(sender, replaceArgs(message, args));
	}

	public static <T> void sendMessage(CommandSender sender, String message, Map<String, T> args) {
		// replace message arguments and then send:
		sendMessage(sender, replaceArgs(message, args));
	}
}
