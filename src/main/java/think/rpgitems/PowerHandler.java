package think.rpgitems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import think.rpgitems.commands.CommandDocumentation;
import think.rpgitems.commands.CommandGroup;
import think.rpgitems.commands.CommandHandler;
import think.rpgitems.commands.CommandString;
import think.rpgitems.data.Locale;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;
import think.rpgitems.power.PowerArrow;
import think.rpgitems.power.PowerCommand;
import think.rpgitems.power.PowerConsume;

public class PowerHandler implements CommandHandler{
    
    @CommandString("rpgitem $n[] power arrow")
    @CommandDocumentation("$COMMAND_RPGITEM_ARROW")
    @CommandGroup("item_power_arrow")
    public void arrow(CommandSender sender, RPGItem item) {
        PowerArrow pow = new PowerArrow();
        pow.cooldownTime = 20;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power arrow $COOLDOWN:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ARROW_FULL")
    @CommandGroup("item_power_arrow")
    public void arrow(CommandSender sender, RPGItem item, int cooldown) {
        PowerArrow pow = new PowerArrow();
        pow.item = item;
        pow.cooldownTime = cooldown;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power command $COOLDOWN:i[] $o[left,right] $DISPLAY:s[] $COMMAND:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_COMMAND")
    @CommandGroup("item_power_command_a")
    public void command(CommandSender sender, RPGItem item, int cooldown, String mouse, String displayText, String command) {
        PowerCommand com = new PowerCommand();
        com.cooldownTime = cooldown;
        command = command.trim();
        if (command.charAt(0) == '/') {
            command = command.substring(1);
        }
        com.isRight = mouse.equals("right");
        com.display = displayText;
        com.command = command;
        com.item = item;
        item.addPower(com);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power command $COOLDOWN:i[] $o[left,right] $DISPLAY:s[] $COMMAND:s[] $PERMISSION:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_COMMAND_FULL")
    @CommandGroup("item_power_command_a")
    public void command(CommandSender sender, RPGItem item, int cooldown, String mouse, String displayText, String command, String permission) {
        PowerCommand com = new PowerCommand();
        com.cooldownTime = cooldown;
        command = command.trim();
        if (command.charAt(0) == '/') {
            command = command.substring(1);
        }
        com.isRight = mouse.equals("right");
        com.display = displayText;
        com.command = command;
        com.permission = permission;
        com.item = item;
        item.addPower(com);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power command $COOLDOWN:i[] $o[left,right] $DETAILS:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_COMMAND_OLD")
    @CommandGroup("item_power_command_b")
    public void command(CommandSender sender, RPGItem item, int cooldown, String mouse, String details) {
        String[] pArgs = details.split("\\|");
        if (pArgs.length < 2) {
            sender.sendMessage(ChatColor.RED + Locale.get("MESSAGE_ERROR_COMMAND_FORMAT"));
            return;
        }
        String display = pArgs[0].trim();
        String command = pArgs[1].trim();
        if (command.charAt(0) == '/') {
            command = command.substring(1);
        }
        String permission = "";
        if (pArgs.length > 2) {
            permission = pArgs[2].trim();
        }

        PowerCommand com = new PowerCommand();
        com.cooldownTime = cooldown;

        com.isRight = mouse.equals("right");
        com.item = item;
        com.display = display;
        com.command = command;
        com.permission = permission;

        item.addPower(com);
        item.rebuild();
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));        
    }
    
    @CommandString("rpgitem $n[] power consume")
    @CommandDocumentation("$COMMAND_RPGITEM_CONSUME")
    @CommandGroup("item_power_consume")
    public void consume(CommandSender sender, RPGItem item) {
        PowerConsume pow = new PowerConsume();
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
}
