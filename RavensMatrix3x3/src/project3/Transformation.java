package project3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

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

	public static int isReflected(String from,String to,String shape) {
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

	public static int isMirrored(String from,String to,String shape) {
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

	public static int isRotated(String from,String to,String shape) {
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
	
	public static ArrayList<String> globalRule(SemanticNet[] sA,SemanticNet[] sB) {
		ArrayList<String> rule = new ArrayList<>(); 
		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (int i=1; i<=3; i++) {
			as.add(Integer.toString(i));
			bs.add(Integer.toString(i));
		}

		Comparator<RavensObjectMatch> comparator = new RavensObjectMatchComparator();
		PriorityQueue<RavensObjectMatch> pq = new PriorityQueue<RavensObjectMatch>(100, comparator);

		for (int i=1; i<=3; i++) {
			for (int j=1; j<=3; j++) {
				RavensObjectMatch m = new RavensObjectMatch(Integer.toString(i)+Integer.toString(j));
				m.score = sA[i].testSimilarity(sB[j]);
				pq.add(m);
			}
		}
		
		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			String a = Character.toString(m.name.charAt(0));
			String b = Character.toString(m.name.charAt(1));
			//System.out.println(m.name);
			
			if (as.isEmpty() || bs.isEmpty()) break;
			if (!as.contains(a) || !bs.contains(b)) continue;
			as.remove(a);
			bs.remove(b);
			
			rule.add(m.name);
			
		}
		return rule;
	}
	
}