package game;

public class Vector {
	public static final Vector zeroVector = new Vector(0, 0);
	
	public final float x, y;
	
	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(float a) {
		this((float) Math.cos(a), (float) Math.sin(a));
	}
	
	public float angle() {
		return (float) Math.atan2(y, x);
	}
	
	public float magnitude() {
		return (float) Math.sqrt(x*x + y*y);
	}
	
	public Vector direction() {
		float mag = magnitude();
		if (mag == 0) {
			return zeroVector;
		} else {
			return this.scale(1/mag);
		}
	}
	
	public Vector scale(float s) {
		return new Vector(x*s, y*s);
	}
	
	public Vector negate() {
		return new Vector(-x, -y);
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y);
	}
	
	public Vector sub(Vector v) {
		return this.add(v.negate());
	}
	
	public float dot(Vector v) {
		return x*v.x + y*v.y;
	}
	
	public Vector rotate(float a) {
		if (this.equals(zeroVector)) {
			return zeroVector;
		}
		
		return new Vector(a + angle()).scale(magnitude());
	}
	
	public float distance(Vector v) {
		return this.sub(v).magnitude();
	}
	
	public Vector perpCCW() {
		return new Vector(-y, x);
	}
	
	public Vector perpCW() {
		return new Vector(y, -x);
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			Vector v = (Vector) o;
			return v.x == x && v.y == y;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Float.hashCode(x) ^ Float.hashCode(y);
	}
	
	@Override
	public String toString() {
		return "<" + x + ", " + y + ">";
	}
}
