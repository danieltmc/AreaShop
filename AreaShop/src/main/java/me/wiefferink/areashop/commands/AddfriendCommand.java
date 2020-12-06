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
import org.bukkit.ChatColor;

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
    // If no perms -> Failure message + return
    if (!sender.hasPermission("areashop.addfriend") && !sender.hasPermission("areashop.addfriendall")) {
      this.plugin.message(sender, "addfriend-noPermission", new Object[0]);
      return;
    }
    // If no username given -> Failure message + return
    if (args.length < 2) {
      this.plugin.message(sender, "addfriend-help", new Object[0]);
      return;
    } 
    if (args.length <= 2) {
      if (sender instanceof Player) {
        List<GeneralRegion> regions = Utils.getImportantRegions(((Player)sender).getLocation());
        // If no region -> Failure message + return
        if (regions.isEmpty()) {
          this.plugin.message(sender, "cmd-noRegionsAtLocation", new Object[0]);
          return;
        } 
        // If multiple regions -> Failure message + return
        if (regions.size() > 1) {
          this.plugin.message(sender, "cmd-moreRegionsAtLocation", new Object[0]);
          return;
        } 
        region = regions.get(0);
      // If command user is not a player -> Failure message + return
      } else {
        this.plugin.message(sender, "cmd-automaticRegionOnlyByPlayer", new Object[0]);
        return;
      } 
    } else {
      region = this.plugin.getFileManager().getRegion(args[2]);
      // If region doesn't exist -> Failure message + return
      if (region == null) {
        this.plugin.message(sender, "cmd-notRegistered", new Object[] { args[2] });
        return;
      } 
    } 
    if (sender.hasPermission("areashop.addfriendall")) {
      // If region is not rented or sold -> Failure message + return
      if ((region instanceof RentRegion && !((RentRegion)region).isRented()) || (region instanceof BuyRegion && 
        !((BuyRegion)region).isSold())) {
        this.plugin.message(sender, "addfriend-noOwner", new Object[] { region });
        return;
      } 
      OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);
      // If friend has not played -> Failure message + return
      if (friend.getLastPlayed() == 0L && !friend.isOnline() && !this.plugin.getConfig().getBoolean("addFriendNotExistingPlayers")) {
        this.plugin.message(sender, "addfriend-notVisited", new Object[] { args[1], region });
        return;
      } 
      // If friend is already on plot -> Failure message + return
      if (region.getFriendsFeature().getFriends().contains(friend.getUniqueId())) {
        this.plugin.message(sender, "addfriend-alreadyAdded", new Object[] { friend.getName(), region });
        return;
      } 
      // If friend is command user -> Failure message + return
      if (region.isOwner(friend.getUniqueId())) {
        this.plugin.message(sender, "addfriend-self", new Object[] { friend.getName(), region });
        return;
      } 
      // If player balance is at least $2500
      if (plugin.getEconomy().getBalance((OfflinePlayer) sender) >= (double) 2500) {
        // If friend was successfully added -> Withdraw $2500 + Success message + return
        if (region.getFriendsFeature().addFriend(friend.getUniqueId(), sender)) {
          plugin.getEconomy().withdrawPlayer((OfflinePlayer) sender, (double) 2500);
          region.update();
          this.plugin.message(sender, "addfriend-successOther", new Object[] { friend.getName(), region });
          return;
        }
        // If friend was not added -> Failure message + return
        else {
          this.plugin.message(sender, "addfriend-noPermission", new Object[0]);
          return;
        }
      }
      // If player has less than $2500 -> Failure message + return
      else {
        this.plugin.message(sender, "addfriend-youBroke");
        return;
      } 
    } else if (sender.hasPermission("areashop.addfriend") && sender instanceof Player) {
      if (region.isOwner((OfflinePlayer)sender)) {
        OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);
        // If friend has not played -> Failure message + return
        if (friend.getLastPlayed() == 0L && !friend.isOnline() && !this.plugin.getConfig().getBoolean("addFriendNotExistingPlayers")) {
          this.plugin.message(sender, "addfriend-notVisited", new Object[] { args[1], region });
          return;
        } 
        // If friend is already on plot -> Failure message + return
        if (region.getFriendsFeature().getFriends().contains(friend.getUniqueId())) {
          this.plugin.message(sender, "addfriend-alreadyAdded", new Object[] { friend.getName(), region });
          return;
        } 
        // If friend is command user -> Failure message + return
        if (region.isOwner(friend.getUniqueId())) {
          this.plugin.message(sender, "addfriend-self", new Object[] { friend.getName(), region });
          return;
        } 
        // If player balance is at least $2500
        if (plugin.getEconomy().getBalance((OfflinePlayer) sender) >= (double) 2500) {
          // If friend was successfully added -> Withdraw $2500 + Success message + return
          if (region.getFriendsFeature().addFriend(friend.getUniqueId(), sender)) {
	          plugin.getEconomy().withdrawPlayer((OfflinePlayer) sender, (double) 2500);
            region.update();
            this.plugin.message(sender, "addfriend-success", new Object[] { friend.getName(), region });
            return;
          }
          // If friend was not added -> Failure message + return
          else {
            this.plugin.message(sender, "addfriend-noPermission", new Object[0]);
            return;
          }
        }
        // If player has less than $2500 -> Failure message + return
        else {
          this.plugin.message(sender, "addFriend-youBroke");
          return;
        }
      }
      else {
        this.plugin.message(sender, "addfriend-noPermissionOther", new Object[] { region });
      } 
    } else {
      this.plugin.message(sender, "addfriend-noPermission", new Object[] { region });
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
