package me.wiefferink.areashop.tools;

import me.wiefferink.areashop.AreaShop;
import me.wiefferink.areashop.regions.BuyRegion;
import me.wiefferink.areashop.regions.GeneralRegion;
import me.wiefferink.areashop.regions.RentRegion;

import java.util.HashMap;

public class Analytics {

	private Analytics() {

	}

	/**
	 * Start analytics tracking.
	 */
	public static void start() {
		// bStats statistics
		AreaShop.debug("WE DO NOT USE bstats.org statistics service");
	}

	private static class RegionStateStats {
		int forrent = 0;
		int forsale = 0;
		int rented = 0;
		int sold = 0;
		int reselling = 0;
	}

	private static RegionStateStats getStateStats() {
		RegionStateStats result = new RegionStateStats();
		for(GeneralRegion region : AreaShop.getInstance().getFileManager().getRegions()) {
			if(region instanceof RentRegion) {
				RentRegion rent = (RentRegion)region;
				if(rent.isAvailable()) {
					result.forrent++;
				} else {
					result.rented++;
				}
			} else if(region instanceof BuyRegion) {
				BuyRegion buy = (BuyRegion)region;
				if(buy.isAvailable()) {
					result.forsale++;
				} else if(buy.isInResellingMode()) {
					result.reselling++;
				} else {
					result.sold++;
				}
			}
		}
		return result;
	}

}
