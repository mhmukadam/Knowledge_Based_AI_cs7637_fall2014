package project2;

import java.util.HashMap;

public class Shape {

	public String name;
	public int sides;
	public int angle; // No apparent difference wrt direction of rotation for multiples of this angle
	public HashMap<String,Integer> rotate;
	public HashMap<String,Integer> reflect;
	public HashMap<String,Integer> mirror;

	public Shape(String name) {
		this.name = name;
	}

	public Shape(String name,int sides,int angle) {
		this.name = name;
		this.sides = sides;
		this.angle = angle;
		this.rotate = new HashMap<>();
		this.reflect = new HashMap<>();
		this.mirror = new HashMap<>();
	}
	
	public void addRotate(String angle,Integer val) {
		this.rotate.put(angle,val);
	}

	public void addReflect(String angle,Integer val) {
		this.reflect.put(angle,val);
	}

	public void addMirror(String angle,Integer val) {
		this.mirror.put(angle,val);
	}

	public int isReflection(String from, String to) {
		// 0 - not reflected, 1 - reflected
		if (this.reflect.get("all") != null) return this.reflect.get("all");
		else if (from.equals(to)) {
			return 0;
		}
		else if (Integer.parseInt(from)>Integer.parseInt(to)) {
			String temp = from;
			from = to;
			to = temp;
		}
		if (this.reflect.get(from+","+to) != null) return this.reflect.get(from+","+to);
		else return 0;
	}

	public int isMirror(String from,String to) {
		// 0 - not mirrored, 1 - mirrored
		if (this.mirror.get("all") != null) return this.mirror.get("all");
		else if (from.equals(to)) {
			return 0;
		}
		else if (Integer.parseInt(from)>Integer.parseInt(to)) {
			String temp = from;
			from = to;
			to = temp;
		}
		if (this.mirror.get(from+","+to) != null) return this.mirror.get(from+","+to);
		else return 0;
	}
	
	public int isRotate(String from,String to) {
		// 0 - rotation not found, 1 - unaffected to rotation
		if (this.rotate.get("all") != null) return this.rotate.get("all");
		else if (from.equals(to)) {
			return 0;
		}
		else if (Integer.parseInt(from)>Integer.parseInt(to)) {
			String temp = from;
			from = to;
			to = temp;
		}
		if (this.rotate.get(from+","+to) != null) return this.rotate.get(from+","+to);
		else return 0;
	}
}