package nl.thewgbbroz.butils_v2.utils;

public class Point3D {
	public double x, y, z;
	
	public Point3D() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(Point3D other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof Point3D))
			return super.equals(obj);
		
		Point3D other = (Point3D) obj;
		
		return other.x == this.x &&
				other.y == this.y &&
				other.z == this.z;
	}
	
	@Override
	protected Point3D clone() throws CloneNotSupportedException {
		return new Point3D(this);
	}
}
