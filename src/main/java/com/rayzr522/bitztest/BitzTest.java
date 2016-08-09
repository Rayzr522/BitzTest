
package com.rayzr522.bitztest;

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

		new Command("nameitem", (sender, command, cmd, args) -> {

			if (!CommandUtils.isPlayer(sender)) {
				messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
				return true;
			}

			Player p = (Player) sender;

			p.getInventory().addItem(ItemUtils.makeItem("nether star, named&cWelcome &b" + p.getName() + "&b!"));

			return true;

		}).executorFor(getCommand("nameitem"));

	}

}
