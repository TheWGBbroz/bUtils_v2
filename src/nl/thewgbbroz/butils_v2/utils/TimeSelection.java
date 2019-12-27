package nl.thewgbbroz.butils_v2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeSelection {
	private final boolean canGoNegative;
	private final List<Unit> allowedUnits;
	
	private long seconds;
	
	private Map<Unit, Long> subdivided = null;
	
	/**
	 * @param seconds The amount of seconds to start with
	 * @param canGoNegative Whether or not this time selection can go negative
	 * @param allowedUnits The allowed units of this time selection
	 */
	public TimeSelection(long seconds, boolean canGoNegative, Unit... allowedUnits) {
		this.canGoNegative = canGoNegative;
		
		if(allowedUnits.length == 0)
			allowedUnits = Unit.values();
		this.allowedUnits = Collections.unmodifiableList(ArrayUtils.arrayToArrayList(allowedUnits));
		
		setTotalSeconds(seconds);
	}
	
	/**
	 * @param seconds The amount of seconds to start with
	 * @param allowedUnits The allowed units of this time selection
	 * 
	 * This will make a time selection which cannot go negative.
	 */
	public TimeSelection(long seconds, Unit... allowedUnits) {
		this(seconds, false, allowedUnits);
	}
	
	/**
	 * @return Whether or not this {@link TimeSelection} can go negative.
	 */
	public boolean canGoNegative() {
		return canGoNegative;
	}
	
	/**
	 * @return A non-modifiable list of the allowed units.
	 */
	public List<Unit> getAllowedUnits() {
		return allowedUnits;
	}
	
	/**
	 * @return The total seconds of this time selection.
	 */
	public long getTotalSeconds() {
		return seconds;
	}
	
	/**
	 * @return The total milliseconds of this time selection.
	 */
	public long getTotalMilliseconds() {
		return seconds * 1000L;
	}
	
	/**
	 * @param seconds The seconds to set the time selection to.
	 */
	public void setTotalSeconds(long seconds) {
		if(!canGoNegative && seconds < 0)
			seconds = 0;
		
		this.seconds = seconds;
	}
	
	/**
	 * @param milliseconds The milliseconds to set the time selection to.
	 */
	public void setTotalMilliseconds(long milliseconds) {
		setTotalSeconds(milliseconds / 1000L);
	}
	
	/**
	 * @return A map with the units and how much of them are in the time selection.
	 */
	public Map<Unit, Long> subdivided() {
		if(subdivided == null) {
			subdivided = new HashMap<>();
			
			long value = this.seconds;
			
			for(Unit unit : Unit.sorted()) {
				if(!allowedUnits.contains(unit))
					continue;
				
				long numUnits = value / unit.secondsInUnit;
				value -= numUnits * unit.secondsInUnit;
				
				subdivided.put(unit, numUnits);
			}
		}
		
		return Collections.unmodifiableMap(subdivided);
	}
	
	/**
	 * @return Alias for {@link HashMap#getOrDefault(unit, 0L)} on the subdivided map.
	 */
	public long getSubdivided(Unit u) {
		return subdivided().getOrDefault(u, 0L);
	}
	
	/**
	 * @param unit The unit the amount is in
	 * @param amount The amount of the units
	 * 
	 * Adds the specified amount of the specified unit to the time selection.
	 */
	public void addUnits(Unit unit, long amount) {
		if(!allowedUnits.contains(unit))
			throw new IllegalArgumentException("That unit is not allowed!");
		
		seconds += amount * unit.secondsInUnit;
		if(!canGoNegative && seconds < 0)
			seconds = 0;
		
		subdivided = null;
	}
	
	/**
	 * @param unit The unit the amount is in
	 * @param amount The amount of the units
	 * 
	 * Removes the specified amount of the specified unit to the time selection.
	 */
	public void removeUnits(Unit unit, long amount) {
		addUnits(unit, -amount);
	}
	
	/**
	 * @return A representation of this time selection in friendly, human readable text.
	 * 
	 * Example:
	 * 1 year, 2 days, 13 hours and 30 seconds
	 */
	public String toFriendlyText() {
		StringBuilder sb = new StringBuilder();
		
		subdivided();
		
		List<Unit> units = new ArrayList<>(Unit.sorted());
		units.removeIf(u -> (!allowedUnits.contains(u) || subdivided.get(u) == 0));
		for(int i = 0; i < units.size(); i++) {
			if(i == units.size() - 1 && units.size() > 1) { // last
				sb.append(" and ");
			}else if(i != 0) { // not first
				sb.append(", ");
			}
			
			Unit unit = units.get(i);
			long numUnits = subdivided.get(unit);
			
			sb.append(unit.friendlyName(numUnits));
		}
		
		return sb.toString();
	}
	
	/**
	 * @param seconds The amount of seconds
	 * @param allowedUnits The allowed units
	 * 
	 * @return The {@link TimeSelection#toFriendlyText()} method of a temporary, newly generated time selection object.
	 */
	public static String toFriendlyText(long seconds, Unit... allowedUnits) {
		return new TimeSelection(seconds, allowedUnits).toFriendlyText();
	}
	
	public enum Unit {
		YEARS(60L * 60L * 24L * 30L * 12L, "year", "years"),
		MONTHS(60L * 60L * 24L * 30L, "month", "months"),
		WEEKS(60L * 60L * 24L * 7L, "week", "weeks"),
		DAYS(60L * 60L * 24L, "day", "days"),
		HOURS(60L * 60L, "hour", "hours"),
		MINUTES(60L, "minute", "minutes"),
		SECONDS(1L, "second", "seconds");
		
		private static List<Unit> sorted;
		
		/**
		 * @return A list with all units, sorted from biggest (years) first, to smallest (seconds) last.
		 */
		public static List<Unit> sorted() {
			if(sorted == null) {
				// Sort
				sorted = new ArrayList<>();
				for(Unit u : values()) sorted.add(u);
				
				Collections.sort(sorted, (u1, u2) -> {
					return (int) (u2.secondsInUnit - u1.secondsInUnit);
				});
			}
			
			return Collections.unmodifiableList(sorted);
		}
		
		/**
		 * The amount of seconds in this unit.
		 */
		public final long secondsInUnit;
		
		/**
		 * The singular and plural name of this unit.
		 */
		public final String singularName;
		public final String pluralName;
		
		private Unit(long secondsInUnit, String singularName, String plurarName) {
			this.secondsInUnit = secondsInUnit;
			this.singularName = singularName;
			this.pluralName = plurarName;
		}
		
		/**
		 * @param numUnits The amount of units
		 * @return The friendly, correct name of this unit with the specified amount
		 * 
		 * Examples:
		 * 4 seconds
		 * 1 day
		 */
		public String friendlyName(long numUnits) {
			return String.valueOf(numUnits) + " " + (numUnits == 1 ? singularName : pluralName);
		}
	}
}
