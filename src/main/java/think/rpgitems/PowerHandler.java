package think.rpgitems;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.potion.PotionEffectType;

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
import think.rpgitems.power.PowerFireball;
import think.rpgitems.power.PowerFlame;
import think.rpgitems.power.PowerIce;
import think.rpgitems.power.PowerKnockup;
import think.rpgitems.power.PowerLightning;
import think.rpgitems.power.PowerPotionHit;
import think.rpgitems.power.PowerPotionSelf;
import think.rpgitems.power.PowerRainbow;
import think.rpgitems.power.PowerRumble;
import think.rpgitems.power.PowerTNTCannon;
import think.rpgitems.power.PowerTeleport;
import think.rpgitems.power.PowerUnbreakable;
import think.rpgitems.power.PowerUnbreaking;

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
    @CommandGroup("item_power_command_b")
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
    @CommandGroup("item_power_command_c")
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
    
    @CommandString("rpgitem $n[] power fireball")
    @CommandDocumentation("$COMMAND_RPGITEM_FIREBALL")
    @CommandGroup("item_power_fireball")
    public void fireball(CommandSender sender, RPGItem item) {
        PowerFireball pow = new PowerFireball();
        pow.cooldownTime = 20;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power fireball $COOLDOWN:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_FIREBALL_FULL")
    @CommandGroup("item_power_fireball")
    public void fireball(CommandSender sender, RPGItem item, int cooldown) {
        PowerFireball pow = new PowerFireball();
        pow.item = item;
        pow.cooldownTime = cooldown;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power flame")
    @CommandDocumentation("$COMMAND_RPGITEM_FLAME")
    @CommandGroup("item_power_flame")
    public void flame(CommandSender sender, RPGItem item) {
        PowerFlame pow = new PowerFlame();
        pow.burnTime = 20;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power flame $BURNTIME:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_FLAME_FULL")
    @CommandGroup("item_power_flame")
    public void flame(CommandSender sender, RPGItem item, int burnTime) {
        PowerFlame pow = new PowerFlame();
        pow.item = item;
        pow.burnTime = burnTime;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power ice")
    @CommandDocumentation("$COMMAND_RPGITEM_ICE")
    @CommandGroup("item_power_ice")
    public void ice(CommandSender sender, RPGItem item) {
        PowerIce pow = new PowerIce();
        pow.cooldownTime = 20;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power ice $COOLDOWN:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_ICE_FULL")
    @CommandGroup("item_power_ice")
    public void ice(CommandSender sender, RPGItem item, int cooldown) {
        PowerIce pow = new PowerIce();
        pow.item = item;
        pow.cooldownTime = cooldown;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power knockup")
    @CommandDocumentation("$COMMAND_RPGITEM_KNOCKUP")
    @CommandGroup("item_power_knockup")
    public void knockup(CommandSender sender, RPGItem item) {
        PowerKnockup pow = new PowerKnockup();
        pow.item = item;
        pow.chance = 20;
        pow.power = 2;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power knockup $CHANCE:i[] $POWER:f[]")
    @CommandDocumentation("$COMMAND_RPGITEM_KNOCKUP_FULL")
    @CommandGroup("item_power_knockup")
    public void knockup(CommandSender sender, RPGItem item, int chance, double power) {
        PowerKnockup pow = new PowerKnockup();
        pow.item = item;
        pow.chance = chance;
        pow.power = power;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power lightning")
    @CommandDocumentation("$COMMAND_RPGITEM_LIGHTNING")
    @CommandGroup("item_power_lightning")
    public void lightning(CommandSender sender, RPGItem item) {
        PowerLightning pow = new PowerLightning();
        pow.item = item;
        pow.chance = 20;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power lightning $CHANCE:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_LIGHTNING_FULL")
    @CommandGroup("item_power_lightning")
    public void lightning(CommandSender sender, RPGItem item, int chance) {
        PowerLightning pow = new PowerLightning();
        pow.item = item;
        pow.chance = chance;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power potionhit $CHANCE:i[] $DURATION:i[] $AMPLIFIER:i[] $EFFECT:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_POTIONHIT+PotionEffectType")
    @CommandGroup("item_power_potionhit")
    public void potionhit(CommandSender sender, RPGItem item, int chance, int duration, int amplifier, String effect) {
        PowerPotionHit pow = new PowerPotionHit();
        pow.item = item;
        pow.chance = chance;
        pow.duration = duration;
        pow.amplifier = amplifier;
        pow.type = PotionEffectType.getByName(effect);
        if (pow.type == null) {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_ERROR_EFFECT"), effect));
            return;
        }
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power potionself $COOLDOWN:i[] $DURATION:i[] $AMPLIFIER:i[] $EFFECT:s[]")
    @CommandDocumentation("$COMMAND_RPGITEM_POTIONSELF+PotionEffectType")
    @CommandGroup("item_power_potionself")
    public void potionself(CommandSender sender, RPGItem item, int ccoldown, int duration, int amplifier, String effect) {
    	PowerPotionSelf pow = new PowerPotionSelf();
        pow.item = item;
        pow.cooldownTime = ccoldown;
        pow.time = duration;
        pow.amplifier = amplifier;
        pow.type = PotionEffectType.getByName(effect);
        if (pow.type == null) {
            sender.sendMessage(ChatColor.RED + String.format(Locale.get("MESSAGE_ERROR_EFFECT"), effect));
            return;
        }
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power rainbow")
    @CommandDocumentation("$COMMAND_RPGITEM_RAINBOW")
    @CommandGroup("item_power_rainbow")
    public void rainbow(CommandSender sender, RPGItem item) {
    	PowerRainbow pow = new PowerRainbow();
        pow.cooldownTime = 20;
        pow.count = 5;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power rainbow $COOLDOWN:i[] $COUNT:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_RAINBOW_FULL")
    @CommandGroup("item_power_rainbow")
    public void rainbow(CommandSender sender, RPGItem item, int cooldown, int count) {
    	PowerRainbow pow = new PowerRainbow();
        pow.cooldownTime = cooldown;
        pow.count = count;
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power rumble $COOLDOWN:i[] $POWER:i[] $DISTANCE:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_RUMBLE")
    @CommandGroup("item_power_rumble")
    public void rumble(CommandSender sender, RPGItem item, int cooldown, int power, int distance) {
    	PowerRumble pow = new PowerRumble();
        pow.item = item;
        pow.cooldownTime = cooldown;
        pow.power = power;
        pow.distance = distance;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power teleport")
    @CommandDocumentation("$COMMAND_RPGITEM_TELEPORT")
    @CommandGroup("item_power_teleport")
    public void teleport(CommandSender sender, RPGItem item) {
    	PowerTeleport pow = new PowerTeleport();
        pow.item = item;
        pow.cooldownTime = 20;
        pow.distance = 5;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }

    @CommandString("rpgitem $n[] power teleport $COOLDOWN:i[] $DISTANCE:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_TELEPORT_FULL")
    @CommandGroup("item_power_teleport")
    public void teleport(CommandSender sender, RPGItem item, int cooldown, int distance) {
    	PowerTeleport pow = new PowerTeleport();
        pow.item = item;
        pow.cooldownTime = cooldown;
        pow.distance = distance;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power tntcannon")
    @CommandDocumentation("$COMMAND_RPGITEM_TNTCANNON")
    @CommandGroup("item_power_tntcannon")
    public void tntcannon(CommandSender sender, RPGItem item) {
    	PowerTNTCannon pow = new PowerTNTCannon();
        pow.item = item;
        pow.cooldownTime = 20;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power tntcannon $COOLDOWN:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_TNTCANNON_FULL")
    @CommandGroup("item_power_tntcannon")
    public void tntcannon(CommandSender sender, RPGItem item, int cooldown) {
    	PowerTNTCannon pow = new PowerTNTCannon();
        pow.item = item;
        pow.cooldownTime = cooldown;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power unbreakable")
    @CommandDocumentation("$COMMAND_RPGITEM_UNBREAKABLE")
    @CommandGroup("item_power_unbreakable")
    public void unbreakable(CommandSender sender, RPGItem item) {
    	PowerUnbreakable pow = new PowerUnbreakable();
        pow.item = item;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power unbreaking")
    @CommandDocumentation("$COMMAND_RPGITEM_UNBREAKING")
    @CommandGroup("item_power_unbreaking")
    public void unbreaking(CommandSender sender, RPGItem item) {
    	PowerUnbreaking pow = new PowerUnbreaking();
        pow.item = item;
        pow.level = 1;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
    
    @CommandString("rpgitem $n[] power unbreaking $LEVEL:i[]")
    @CommandDocumentation("$COMMAND_RPGITEM_UNBREAKING_FULL")
    @CommandGroup("item_power_unbreaking")
    public void unbreaking(CommandSender sender, RPGItem item, int level) {
    	PowerUnbreaking pow = new PowerUnbreaking();
        pow.item = item;
        pow.level = level;
        item.addPower(pow);
        ItemManager.save(Plugin.plugin);
        sender.sendMessage(ChatColor.AQUA + Locale.get("MESSAGE_POWER_OK"));
    }
}
