
package com.rayzr522.bitztest;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rayzr522.bitzapi.BitzPlugin;
import com.rayzr522.bitzapi.message.BitzMessages;
import com.rayzr522.bitzapi.testcommands.Command;
import com.rayzr522.bitzapi.utils.CommandUtils;
import com.rayzr522.bitzapi.utils.item.ItemUtils;

public class BitzTest extends BitzPlugin {

	@Override
	public void onPluginLoad() {

		messenger.setPrefix("&8&l(&7*name&8&l)&r");

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

		Command child = base.addSubcommand("custom", this::subCommand).setUsage("/nameitem custom <type>");

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
