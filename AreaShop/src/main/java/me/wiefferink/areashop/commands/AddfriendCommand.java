package me.wiefferink.areashop.commands;

import java.util.ArrayList;
import java.util.List;
import me.wiefferink.areashop.regions.BuyRegion;
import me.wiefferink.areashop.regions.GeneralRegion;
import me.wiefferink.areashop.regions.RentRegion;
import me.wiefferink.areashop.tools.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.Economy;

public class AddfriendCommand extends CommandAreaShop {
  public String getCommandStart() {
    return "areashop addfriend";
  }
  
  public String getHelp(CommandSender target) {
    if (target.hasPermission("areashop.addfriendall") || target.hasPermission("areashop.addfriend"))
      return "help-addFriend"; 
    return null;
  }
  
  public void execute(CommandSender sender, String[] args) {
    GeneralRegion region;
    if (!sender.hasPermission("areashop.addfriend") && !sender.hasPermission("areashop.addfriendall")) {
      this.plugin.message(sender, "addfriend-noPermission", new Object[0]);
      return;
    } 
    if (args.length < 2) {
      this.plugin.message(sender, "addfriend-help", new Object[0]);
      return;
    } 
    if (args.length <= 2) {
      if (sender instanceof Player) {
        List<GeneralRegion> regions = Utils.getImportantRegions(((Player)sender).getLocation());
        if (regions.isEmpty()) {
          this.plugin.message(sender, "cmd-noRegionsAtLocation", new Object[0]);
          return;
        } 
        if (regions.size() > 1) {
          this.plugin.message(sender, "cmd-moreRegionsAtLocation", new Object[0]);
          return;
        } 
        region = regions.get(0);
      } else {
        this.plugin.message(sender, "cmd-automaticRegionOnlyByPlayer", new Object[0]);
        return;
      } 
    } else {
      region = this.plugin.getFileManager().getRegion(args[2]);
      if (region == null) {
        this.plugin.message(sender, "cmd-notRegistered", new Object[] { args[2] });
        return;
      } 
    } 
    if (sender.hasPermission("areashop.addfriendall")) {
      if ((region instanceof RentRegion && !((RentRegion)region).isRented()) || (region instanceof BuyRegion && 
        !((BuyRegion)region).isSold())) {
        this.plugin.message(sender, "addfriend-noOwner", new Object[] { region });
        return;
      } 
      OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);
      if (friend.getLastPlayed() == 0L && !friend.isOnline() && !this.plugin.getConfig().getBoolean("addFriendNotExistingPlayers")) {
        this.plugin.message(sender, "addfriend-notVisited", new Object[] { args[1], region });
        return;
      } 
      if (region.getFriendsFeature().getFriends().contains(friend.getUniqueId())) {
        this.plugin.message(sender, "addfriend-alreadyAdded", new Object[] { friend.getName(), region });
        return;
      } 
      if (region.isOwner(friend.getUniqueId())) {
        this.plugin.message(sender, "addfriend-self", new Object[] { friend.getName(), region });
        return;
      } 
      if (region.getFriendsFeature().addFriend(friend.getUniqueId(), sender)) {
	        if (plugin.getEconomy().getBalance((OfflinePlayer) sender) >= (double) 2500) {
	          plugin.getEconomy().withdrawPlayer((OfflinePlayer) sender, (double) 2500);
	          region.update();
	          this.plugin.message(sender, "addfriend-successOther", new Object[] { friend.getName(), region });
	        }
      } 
    } else if (sender.hasPermission("areashop.addfriend") && sender instanceof Player) {
      if (region.isOwner((OfflinePlayer)sender)) {
        OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);
        if (friend.getLastPlayed() == 0L && !friend.isOnline() && !this.plugin.getConfig().getBoolean("addFriendNotExistingPlayers")) {
          this.plugin.message(sender, "addfriend-notVisited", new Object[] { args[1], region });
          return;
        } 
        if (region.getFriendsFeature().getFriends().contains(friend.getUniqueId())) {
          this.plugin.message(sender, "addfriend-alreadyAdded", new Object[] { friend.getName(), region });
          return;
        } 
        if (region.isOwner(friend.getUniqueId())) {
          this.plugin.message(sender, "addfriend-self", new Object[] { friend.getName(), region });
          return;
        } 
        if (region.getFriendsFeature().addFriend(friend.getUniqueId(), sender)) {
        	if (plugin.getEconomy().getBalance((OfflinePlayer) sender) >= (double) 2500) {
	            plugin.getEconomy().withdrawPlayer((OfflinePlayer) sender, (double) 2500);
          region.update();
          this.plugin.message(sender, "addfriend-success", new Object[] { friend.getName(), region });
        } 
      } else {
        this.plugin.message(sender, "addfriend-noPermissionOther", new Object[] { region });
      } 
    } else {
      this.plugin.message(sender, "addfriend-noPermission", new Object[] { region });
    } 
    }
  }
  
  public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
    ArrayList<String> result = new ArrayList<>();
    if (toComplete == 2) {
      for (Player player : Utils.getOnlinePlayers())
        result.add(player.getName()); 
    } else if (toComplete == 3) {
      result.addAll(this.plugin.getFileManager().getRegionNames());
    } 
    return result;
  }
}
