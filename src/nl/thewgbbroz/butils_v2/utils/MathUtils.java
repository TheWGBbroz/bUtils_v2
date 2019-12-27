package nl.thewgbbroz.butils_v2.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class MathUtils {
	private MathUtils() {
	}
	
	public static double round(double value, int places) {
		if(places < 0)
			throw new IllegalArgumentException();
		
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	
	public static double map(double n, double nMin, double nMax, double newMin, double newMax) {
		return (n - nMin) / (nMax - nMin) * (newMax - newMin) + newMin;
	}
	
	public static double clamp(double n, double min, double max) {
		if(n < min) return min;
		if(n > max) return max;
		
		return n;
	}
	
	public static int clamp(int n, int min, int max) {
		if(n < min) return min;
		if(n > max) return max;
		
		return n;
	}
	
	public static boolean isInt(String s) {
		try {
			Integer.valueOf(s);
			return true;
		}catch(NumberFormatException ex) {}
		
		return false;
	}
	
	public static boolean isDouble(String s) {
		try {
			Double.valueOf(s);
			return true;
		}catch(NumberFormatException ex) {}
		
		return false;
	}
	
	public static double distSq(double x1, double y1, double x2, double y2) {
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	
	public static double dist(double x1, double y1, double x2, double y2) {
		return Math.sqrt(distSq(x1, y1, x2, y2));
	}
	
	public static double distSq(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
	}
	
	public static double dist(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(distSq(x1, y1, z1, x2, y2, z2));
	}
	
	public static EulerAngle calculateEulerAngle(Vector vector, double pitchOffset, double yawOffset, double rollOffset) {
		vector = vector.clone();
		vector.normalize();
		
		// Minecraft world conversion
		yawOffset += -Math.PI / 2.0;
		
		double pitch = Math.asin(-vector.getY()) + pitchOffset;
		double yaw   = Math.atan2(vector.getZ(), vector.getX()) + yawOffset;
		double roll  = 0 + rollOffset;
		
		return new EulerAngle(pitch, yaw, roll);
	}
	
	public static EulerAngle calculateEulerAngle(Vector vector) {
		return calculateEulerAngle(vector, 0, 0, 0);
	}
}
