
package com.rayzr522.bitztest;

import static mirror.Mirror.$;
import static org.bukkit.ChatColor.BLUE;
import static org.bukkit.ChatColor.GREEN;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rayzr522.bitzapi.BitzPlugin;
import com.rayzr522.bitzapi.message.BitzMessages;
import com.rayzr522.bitzapi.testcommands.Command;
import com.rayzr522.bitzapi.utils.CommandUtils;
import com.rayzr522.bitzapi.utils.Reflection;
import com.rayzr522.bitzapi.utils.data.LoreData;
import com.rayzr522.bitzapi.utils.item.ItemUtils;

import mirror.PacketBuilder;
import mirror.PlayerWrapper;
import mkremins.fanciful.FancyMessage;

public class BitzTest extends BitzPlugin {

    @Override
    public void onPluginLoad() {

        messenger.setPrefix("&8&l(&7{name}&8&l)&r");

        configManager.ensureFolderExists(configManager.getFile("players/"));

        // haxxTheBukkit();

    }

    public void haxxTheBukkit() {

        try {
            Class<?> ITEM = Reflection.getNMS("Item");
            Class<?> MINECRAFT_KEY = Reflection.getNMS("MinecraftKey");
            Object REGISTRY = ITEM.getField("REGISTRY").get(null);

            Object key = MINECRAFT_KEY.getConstructor(String.class).newInstance("minecraft:diamond_sword");
            Object item = $(REGISTRY).getMethod("get", Object.class).invoke(key);

            $(item).setField("maxStackSize", 3);

        } catch (Exception e) {
            System.out.println("Failed to haxx the Bukkit");
            e.printStackTrace();
        }
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

            ItemStack item = ItemUtils.makeItem("nether star, named &cWelcome &b" + p.getName() + "&b!");

            p.getInventory().addItem(item);

            return true;

        }).executorFor(getCommand("nameitem"));

        base.addSubcommand("custom", this::subCommand).setUsage("/nameitem custom <type>");

        new Command("watch", (sender, command, cmd, args) -> {

            if (!CommandUtils.isPlayer(sender)) {

                messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
                return true;

            }

            Player p = (Player) sender;
            PlayerWrapper wrapper = $(p);

            if (args.length < 1) {

                Object packet = new PacketBuilder("PlayOutCamera").set("a", p.getEntityId()).create();
                wrapper.sendPacket(packet);

                p.sendMessage(ChatColor.RED + "Please specify a player!");
                return true;

            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {

                p.sendMessage(ChatColor.RED + "That player could not be found!");
                return true;

            }

            Object packet = new PacketBuilder("PlayOutCamera").set("a", target.getEntityId()).create();

            wrapper.sendPacket(packet);

            return true;

        }).executorFor(getCommand("watch"));

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

            $(p).sendRaw(new FancyMessage("Welcome ").color(GREEN).then(p.getDisplayName()).color(BLUE).then(" to the server!").color(GREEN).tooltip("A stupiod test server").toJSONString());

            try {
                configManager.save(info, "players/" + info.uuid + ".yml");
                p.sendMessage(ChatColor.DARK_PURPLE + "Data saved to players/" + info.uuid + ".yml");
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "An error has occured! Please check the console.");
                e.printStackTrace();
            }

            return true;

        }).executorFor(getCommand("playerInfo"));

        new Command("debug", (sender, command, label, args) -> {

            if (!CommandUtils.isPlayer(sender)) {

                messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
                return true;

            }

            Player p = (Player) sender;

            if (args.length > 0 && args[0].equalsIgnoreCase("item")) {

                ItemStack item = ItemUtils.makeItem("diamond, named &b&lTest &c&nItem!");

                LoreData data = new LoreData(item);
                data.set("testData", p.getHealth());
                data.write(item);

                p.getInventory().addItem(item);

                return true;

            }

            if (!ItemUtils.isEmpty(p.getInventory().getItemInMainHand())) {

                ItemStack item = p.getInventory().getItemInMainHand();

                LoreData data = new LoreData(item);

                data.debug(p);

            }

            return true;

        }).executorFor(getCommand("debug"));

        new Command("rp", (sender, command, label, args) -> {

            if (!CommandUtils.isPlayer(sender)) {

                messenger.playerMessage(sender, BitzMessages.ONLY_PLAYERS.msg);
                return true;

            }

            Player p = (Player) sender;

            String path = configManager.getFile("ItemRarity.zip").getAbsolutePath();
            p.sendMessage("Setting resource pack: " + path);
            p.setResourcePack(path);

            return true;
        }).executorFor(getCommand("rp"));

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
