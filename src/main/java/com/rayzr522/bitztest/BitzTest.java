
package com.rayzr522.bitztest;

import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rayzr522.bitzapi.BitzPlugin;
import com.rayzr522.bitzapi.message.BitzMessages;
import com.rayzr522.bitzapi.testcommands.Command;
import com.rayzr522.bitzapi.utils.CommandUtils;
import com.rayzr522.bitzapi.utils.item.ItemUtils;

import net.md_5.bungee.api.ChatColor;

public class BitzTest extends BitzPlugin {

	@Override
	public void onPluginLoad() {

		messenger.setPrefix("&8&l(&7{name}&8&l)&r");

		configManager.ensureFolderExists(configManager.getFile("players/"));

	}

	@Override
	public void onPluginUnload() {
	}

	@Override
	public void registerCommands() {

		Command base = new Command("nameitem", (sender, command, cmd, args) -> {

			if (!CommandUtils.isPlayer(sender)) {
				messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
				return true;
			}

			Player p = (Player) sender;

			p.getInventory().addItem(ItemUtils.makeItem("nether star, named &cWelcome &b" + p.getName() + "&b!"));

			return true;

		}).executorFor(getCommand("nameitem"));

		base.addSubcommand("custom", this::subCommand).setUsage("/nameitem custom <type>");

		new Command("playerInfo", (sender, command, cmd, args) -> {

			if (!CommandUtils.isPlayer(sender)) {

				messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
				return true;

			}

			Player p = (Player) sender;

			if (args.length >= 1) {

				if (args[0].equalsIgnoreCase("load")) {

					try {

						PlayerInfo info = configManager.load(PlayerInfo.class, "players/" + p.getUniqueId() + ".yml");

						p.sendMessage(ChatColor.GREEN + "Info: " + ChatColor.BLUE + info.toString());

					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Failed to load player info file");
						e.printStackTrace();
					}

				} else {

					sender.sendMessage(ChatColor.RED + "Unknown command '" + args[0] + "'");

				}

				return true;

			}

			// PlayerInfo info = new PlayerInfo((Player) sender);
			PlayerInfo info = new PlayerInfo();

			info.uuid = p.getUniqueId().toString();
			info.money = Math.abs(new Random().nextGaussian() * 50.0);
			info.pos = p.getLocation().toVector();

			try {
				configManager.save(info, "players/" + info.uuid + ".yml");
				p.sendMessage(ChatColor.DARK_PURPLE + "Data saved to players/" + info.uuid + ".yml");
			} catch (Exception e) {
				p.sendMessage(ChatColor.RED + "An error has occured! Please check the console.");
				e.printStackTrace();
			}

			return true;

		}).executorFor(getCommand("playerInfo"));

	}

	public boolean subCommand(CommandSender sender, org.bukkit.command.Command command, String cmd, String[] args) {

		if (!CommandUtils.isPlayer(sender)) {
			messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
			return true;
		}

		if (args.length < 1) {

			messenger.playerMessage(sender, BitzMessages.NO_ARG.msg, "type");
			return false;

		}

		if (ItemUtils.getType(args[0]) == null) {
			messenger.playerInfo(sender, "Unknown type: '" + args[0] + "'");
			return false;
		}

		Player p = (Player) sender;

		p.getInventory().addItem(ItemUtils.makeItem(args[0] + ", named &cWelcome &b" + p.getName() + "&b!"));

		return true;

	}

}
