package think.rpgitems.power;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import think.rpgitems.Plugin;
import think.rpgitems.commands.Commands;
import think.rpgitems.data.Locale;
import think.rpgitems.data.RPGValue;
import think.rpgitems.item.ItemManager;
import think.rpgitems.item.RPGItem;

public class PowerCommand extends Power {

    String command = "";
    String display = "Runs command";
    String permission = "";
    boolean isRight = true;
    long cd = 20;

    @Override
    public void rightClick(Player player) {
        if (isRight) {
            long cooldown;
            RPGValue value = RPGValue.get(player, item, "command." + command + ".cooldown");
            if (value == null) {
                cooldown = System.currentTimeMillis() / 50;
                value = new RPGValue(player, item, "command." + command + ".cooldown", cooldown);
            } else {
                cooldown = value.asLong();
            }
            if (cooldown <= System.currentTimeMillis() / 50) {
                value.set(System.currentTimeMillis() / 50 + cd);
                if (permission.length() != 0)
                    player.addAttachment(Plugin.plugin, 1).setPermission(permission, true);
                player.chat("/" + command);
            } else {
                player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
            }
        }
    }

    @Override
    public void leftClick(Player player) {
        if (!isRight) {
            long cooldown;
            RPGValue value = RPGValue.get(player, item, "command." + command + ".cooldown");
            if (value == null) {
                cooldown = System.currentTimeMillis() / 50;
                value = new RPGValue(player, item, "command." + command + ".cooldown", cooldown);
            } else {
                cooldown = value.asLong();
            }
            if (cooldown <= System.currentTimeMillis() / 50) {
                value.set(System.currentTimeMillis() / 50 + cd);
                if (permission.length() != 0)
                    player.addAttachment(Plugin.plugin, 1).setPermission(permission, true);
                player.chat("/" + command);
            } else {
                player.sendMessage(ChatColor.AQUA + String.format(Locale.get("MESSAGE_COOLDOWN"), ((double) (cooldown - System.currentTimeMillis() / 50)) / 20d));
            }
        }
    }

    public String displayText() {
        return ChatColor.GREEN + display;
    }

    public String getName() {
        return "command";
    }

    public void init(ConfigurationSection s) {
        cd = s.getLong("cooldown", 20);
        command = s.getString("command", "");
        display = s.getString("display", "");
        isRight = s.getBoolean("isRight", true);
        permission = s.getString("permission", "");
    }

    public void save(ConfigurationSection s) {
        s.set("cooldown", cd);
        s.set("command", command);
        s.set("display", display);
        s.set("isRight", isRight);
        s.set("permission", permission);
    }

    static {

        Commands.add("rpgitem $n[] power command $COOLDOWN:i[] $o[left,right] $DISPLAY:s[] $COMMAND:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_COMMAND");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerCommand com = new PowerCommand();
                com.cd = (Integer) args[1];
                String command = ((String) args[4]).trim();
                if (command.charAt(0) == '/') {
                    command = command.substring(1);
                }
                com.isRight = ((String) args[2]).equals("right");
                com.display = (String) args[3];
                com.command = command;
                com.item = item;
                item.addPower(com);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power command $COOLDOWN:i[] $o[left,right] $DISPLAY:s[] $COMMAND:s[] $PERMISSION:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_COMMAND_FULL");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                RPGItem item = (RPGItem) args[0];
                PowerCommand com = new PowerCommand();
                com.cd = (Integer) args[1];
                String command = ((String) args[4]).trim();
                if (command.charAt(0) == '/') {
                    command = command.substring(1);
                }
                com.isRight = ((String) args[2]).equals("right");
                com.display = (String) args[3];
                com.command = command;
                com.permission = (String) args[5];
                com.item = item;
                item.addPower(com);
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
        Commands.add("rpgitem $n[] power command $COOLDOWN:i[] $o[left,right] $DETAILS:s[]", new Commands() {

            @Override
            public String getDocs() {
                return Locale.get("COMMAND_RPGITEM_COMMAND_OLD");
            }

            @Override
            public void command(CommandSender sender, Object[] args) {
                String[] pArgs = ((String) args[3]).split("\\|");
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

                RPGItem item = (RPGItem) args[0];
                PowerCommand com = new PowerCommand();
                com.cd = (Integer) args[1];

                com.isRight = ((String) args[2]).equals("right");
                com.item = item;
                com.display = display;
                com.command = command;
                com.permission = permission;

                item.addPower(com);
                item.rebuild();
                ItemManager.save(Plugin.plugin);
                sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
            }
        });
    }
}
