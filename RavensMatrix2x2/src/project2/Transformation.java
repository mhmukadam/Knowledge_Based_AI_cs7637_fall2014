package project2;

// This class evaluates transformations between objects A ad B based on various attributes
public class Transformation {

	public static String sizeChange(String from,String to) {
		int f = 0;
		int t = 0;

		switch (from) {
		case "very-small": 
			f=1;
			break;
		case "small":
			f=2;
			break;
		case "medium":
			f=3;
			break;
		case "large":
			f=4;
			break;
		case "very-large":
			f=5;
			break;
		default:
			f=10;
			break;
		}
		switch (to) {
		case "very-small": 
			t=1;
			break;
		case "small":
			t=2;
			break;
		case "medium":
			t=3;
			break;
		case "large":
			t=4;
			break;
		case "very-large":
			t=5;
			break;
		default:
			t=10;
			break;
		}

		if (f==10 || t==10) {
			if (from.equals(to)) return Integer.toBinaryString(10);
			else return Integer.toString(-10);
		}
		else return Integer.toString(t-f);
	}

	public static int isReflected(String from, String to,String shape) {
		if (Agent.shapes.get(shape) == null) return 0;
		else {
			int f = Integer.parseInt(from);
			int t = Integer.parseInt(to);
			while (f>360) {
				f-=360;
			}
			while (t>360) {
				t-=360;
			}
			return Agent.shapes.get(shape).isReflection(Integer.toString(f),Integer.toString(t));
		}
	}

	public static int isMirrored(String from, String to,String shape) {
		if (Agent.shapes.get(shape) == null) return 0;
		else {
			int f = Integer.parseInt(from);
			int t = Integer.parseInt(to);
			while (f>360) {
				f-=360;
			}
			while (t>360) {
				t-=360;
			}
			return Agent.shapes.get(shape).isMirror(Integer.toString(f),Integer.toString(t));
		}
	}

	public static int isRotated(String from, String to,String shape) {
		int f = Integer.parseInt(from);
		int t = Integer.parseInt(to);
		while (f>360) {
			f-=360;
		}
		while (t>360) {
			t-=360;
		}
		if (Agent.shapes.get(shape) == null) return (t-f);
		else if (Agent.shapes.get(shape).isRotate(Integer.toString(f),Integer.toString(t)) == 1) return 1;
		else return Transformation.checkDirection((t-f),shape);
	}
	
	public static int checkDirection(int angle,String shape) {
		if (angle % Agent.shapes.get(shape).angle == 0) return Math.abs(angle);
		else return angle;
	}
	
}