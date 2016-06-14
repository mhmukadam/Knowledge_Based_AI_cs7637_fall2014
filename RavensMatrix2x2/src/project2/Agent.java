package project2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
	/**
	 * The default constructor for your Agent. Make sure to execute any
	 * processing necessary before your Agent starts solving problems here.
	 * 
	 * Do not add any variables to this signature; they will not be used by
	 * main().
	 * 
	 */
	public static HashMap<String,Shape> shapes;
	public static int objNo;
	public static ArrayList<Integer> objNoListAB;
	public static ArrayList<Integer> objNoListAC;

	public Agent() {
		shapes = new HashMap<>();
		makeShapes();
		objNo = 97;
		objNoListAB = new ArrayList<>();
		objNoListAC = new ArrayList<>();
	}
	/**
	 * The primary method for solving incoming Raven's Progressive Matrices.
	 * For each problem, your Agent's Solve() method will be called. At the
	 * conclusion of Solve(), your Agent should return a String representing its
	 * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
	 * are also the Names of the individual RavensFigures, obtained through
	 * RavensFigure.getName().
	 * 
	 * In addition to returning your answer at the end of the method, your Agent
	 * may also call problem.checkAnswer(String givenAnswer). The parameter
	 * passed to checkAnswer should be your Agent's current guess for the
	 * problem; checkAnswer will return the correct answer to the problem. This
	 * allows your Agent to check its answer. Note, however, that after your
	 * agent has called checkAnswer, it will *not* be able to change its answer.
	 * checkAnswer is used to allow your Agent to learn from its incorrect
	 * answers; however, your Agent cannot change the answer to a question it
	 * has already answered.
	 * 
	 * If your Agent calls checkAnswer during execution of Solve, the answer it
	 * returns will be ignored; otherwise, the answer returned at the end of
	 * Solve will be taken as your Agent's answer to this problem.
	 * 
	 * @param problem the RavensProblem your agent should solve
	 * @return your Agent's answer to this problem
	 */
	public String Solve(RavensProblem problem) {
		//****************Problem Restriction****************//
		//for debugging one problem at a time
//		if (!problem.getName().equals("2x2 Basic Problem 15")) return "0";
		//***************************************************//
		
		//******************Current Problem******************//
		System.out.println("************************************");
		System.out.println("Solving | "+problem.getName());
		
		RavensFigure oldA = problem.getFigures().get("A");
		RavensFigure oldB = problem.getFigures().get("B");
		RavensFigure oldC = problem.getFigures().get("C");

		MyRavensFigure A = new MyRavensFigure(oldA);
		MyRavensFigure B = new MyRavensFigure(oldB);
		MyRavensFigure C = new MyRavensFigure(oldC);

		findMatchesAB(A,B);
		findMatchesAC(A,C);

		SemanticNet sourceAB = new SemanticNet("AB");
		sourceAB.createSemanticNet(A,B,"horizontal");

		SemanticNet sourceAC = new SemanticNet("AC");
		sourceAC.createSemanticNet(A,C,"vertical");

		ArrayList<MyRavensFigure> I = new ArrayList<>();
		SemanticNet[] sink = new SemanticNet[13];
		int[] simScore = new int[7];
		int maxScore = Integer.MIN_VALUE;
		int maxOption = 0;
		System.out.println("Scores:");
		
		for (int i=1; i<= 6; i++) {
			I.add(new MyRavensFigure(problem.getFigures().get(Integer.toString(i))));
			findMatchesCI(C,I.get(i-1));
			sink[i] = new SemanticNet(Integer.toString(i));
			sink[i].createSemanticNet(C,I.get(i-1),"horizontal");
			simScore[i] = sourceAB.testSimilarity(sink[i]);
			if (problem.getProblemType().equals("2x2")) {
				findMatchesBI(B,I.get(i-1));
				sink[i+6] = new SemanticNet(Integer.toString(i));
				sink[i+6].createSemanticNet(B,I.get(i-1),"vertical");
				simScore[i] += sourceAC.testSimilarity(sink[i]);
			}

			System.out.print(" | "+simScore[i]);
			if (maxScore <= simScore[i]) {
				maxScore = simScore[i];
				maxOption = i;
			}
		}
		System.out.println();
		System.out.println("************************************");
		System.out.println();
		
		// Reset Variables
		objNo = 97;
		objNoListAB = new ArrayList<>();
		objNoListAC = new ArrayList<>();
		
		return Integer.toString(maxOption);
	}

	public static void findMatchesAB(MyRavensFigure A, MyRavensFigure B) {

		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			as.add(eA.getKey());
		}
		for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
			bs.add(eB.getKey());
		}

		Comparator<RavensObjectMatch> comparator = new RavensObjectMatchComparator();
		PriorityQueue<RavensObjectMatch> pq = new PriorityQueue<RavensObjectMatch>(100, comparator);

		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
				RavensObjectMatch m = new RavensObjectMatch(eA.getKey()+eB.getKey());

				ArrayList<String> atB = new ArrayList<>();
				for (Entry<String,String> eBat : eB.getValue().attributes.entrySet()) {
					atB.add(eBat.getKey());
				}

				for (Entry<String,String> eAat : eA.getValue().attributes.entrySet()) {
					String ak = eAat.getKey();
					String av = eAat.getValue();
					String bv = eB.getValue().attributes.get(ak);

					if (bv == null) {
						m.score -= 2;
						continue;
					}
					else if (ak.equals("shape")) {
						if (av.equals(bv)) m.score += 5;
						else m.score -= 5;
					}
					else if (ak.equals("size")) {
						if (av.equals(bv)) m.score += 4;
						else m.score -= 4;
					}
					else if (ak.equals("fill")) {
						if (av.equals(bv)) m.score += 3;
						else m.score -= 3;
					}
					else {
						String[] temp = av.split(",");
						if (av.equals(bv)) m.score += 2*temp.length;
						else m.score -= 1*temp.length;
					}
					atB.remove(ak);
				}
				if (!atB.isEmpty()) m.score -= 2*atB.size();

				pq.add(m);
			}
		}

		int i = 1;
		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			String a = Character.toString(m.name.charAt(0));
			String b = Character.toString(m.name.charAt(1));

			if (as.isEmpty() || bs.isEmpty()) break;
			if (!as.contains(a) || !bs.contains(b)) continue;

			A.changeObjectName(a,Integer.toString(i));
			B.changeObjectName(b,Integer.toString(i));
			i++;
			as.remove(a);
			bs.remove(b);
		}
		if (!as.isEmpty()) {
			Iterator<String> it = as.iterator();
			while(it.hasNext()) {
				String a = it.next();
				A.changeObjectName(a,Character.toString((char)objNo));
				objNoListAB.add(objNo);
				objNoListAC.add(objNo);
				objNo++;
			}
		}
		if (!bs.isEmpty()) {
			Iterator<String> it = bs.iterator();
			while(it.hasNext()) {
				String b = it.next();
				B.changeObjectName(b,Character.toString((char)objNo));
				objNoListAB.add(objNo);
				objNo++;
			}
		}

	}

	public static void printPQ(PriorityQueue<RavensObjectMatch> pq) {
		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			System.out.println(m.name+" : "+m.score);
		}
	}

	public static void findMatchesAC(MyRavensFigure A, MyRavensFigure B) {

		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			as.add(eA.getKey());
		}
		for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
			bs.add(eB.getKey());
		}

		Comparator<RavensObjectMatch> comparator = new RavensObjectMatchComparator();
		PriorityQueue<RavensObjectMatch> pq = new PriorityQueue<RavensObjectMatch>(100, comparator);

		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
				RavensObjectMatch m = new RavensObjectMatch(eA.getKey()+eB.getKey());

				ArrayList<String> atB = new ArrayList<>();
				for (Entry<String,String> eBat : eB.getValue().attributes.entrySet()) {
					atB.add(eBat.getKey());
				}

				for (Entry<String,String> eAat : eA.getValue().attributes.entrySet()) {
					String ak = eAat.getKey();
					String av = eAat.getValue();
					String bv = eB.getValue().attributes.get(ak);

					if (bv == null) {
						m.score -= 2;
						continue;
					}
					else if (ak.equals("shape")) {
						if (av.equals(bv)) m.score += 5;
						else m.score -= 5;
					}
					else if (ak.equals("size")) {
						if (av.equals(bv)) m.score += 4;
						else m.score -= 4;
					}
					else if (ak.equals("fill")) {
						if (av.equals(bv)) m.score += 3;
						else m.score -= 3;
					}
					else {
						String[] temp = av.split(",");
						if (av.equals(bv)) m.score += 2*temp.length;
						else m.score -= 1*temp.length;
					}
					atB.remove(ak);
				}
				if (!atB.isEmpty()) m.score -= 2*atB.size();

				pq.add(m);
			}
		}

		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			String a = Character.toString(m.name.charAt(0));
			String b = Character.toString(m.name.charAt(1));

			if (as.isEmpty() || bs.isEmpty()) break;
			if (!as.contains(a) || !bs.contains(b)) continue;

			B.changeObjectName(b,a);
			as.remove(a);
			bs.remove(b);
		}
		if (!bs.isEmpty()) {
			Iterator<String> it = bs.iterator();
			while(it.hasNext()) {
				String b = it.next();
				B.changeObjectName(b,Character.toString((char)objNo));
				objNoListAC.add(objNo);
				objNo++;
			}
		}
	}
	
	public static void findMatchesCI(MyRavensFigure A, MyRavensFigure B) {
		
		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			as.add(eA.getKey());
		}
		for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
			bs.add(eB.getKey());
		}

		Comparator<RavensObjectMatch> comparator = new RavensObjectMatchComparator();
		PriorityQueue<RavensObjectMatch> pq = new PriorityQueue<RavensObjectMatch>(100, comparator);

		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
				RavensObjectMatch m = new RavensObjectMatch(eA.getKey()+eB.getKey());

				ArrayList<String> atB = new ArrayList<>();
				for (Entry<String,String> eBat : eB.getValue().attributes.entrySet()) {
					atB.add(eBat.getKey());
				}

				for (Entry<String,String> eAat : eA.getValue().attributes.entrySet()) {
					String ak = eAat.getKey();
					String av = eAat.getValue();
					String bv = eB.getValue().attributes.get(ak);

					if (bv == null) {
						m.score -= 2;
						continue;
					}
					else if (ak.equals("shape")) {
						if (av.equals(bv)) m.score += 5;
						else m.score -= 5;
					}
					else if (ak.equals("size")) {
						if (av.equals(bv)) m.score += 4;
						else m.score -= 4;
					}
					else if (ak.equals("fill")) {
						if (av.equals(bv)) m.score += 3;
						else m.score -= 3;
					}
					else {
						String[] temp = av.split(",");
						if (av.equals(bv)) m.score += 2*temp.length;
						else m.score -= 1*temp.length;
					}
					atB.remove(ak);
				}
				if (!atB.isEmpty()) m.score -= 2*atB.size();

				pq.add(m);
			}
		}

		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			String a = Character.toString(m.name.charAt(0));
			String b = Character.toString(m.name.charAt(1));

			if (as.isEmpty() || bs.isEmpty()) break;
			if (!as.contains(a) || !bs.contains(b)) continue;

			B.changeObjectName(b,a);
			as.remove(a);
			bs.remove(b);
		}
		int i = 97;
		if (!bs.isEmpty()) {
			Iterator<String> it = bs.iterator();
			while(it.hasNext()) {
				String b = it.next();
				while(objNoListAC.contains(i)) {
					i++;
				}
				B.changeObjectName(b,Character.toString((char)i));
			}
		}
	}

	public static void findMatchesBI(MyRavensFigure A, MyRavensFigure B) {
		
		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			as.add(eA.getKey());
		}
		for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
			bs.add(eB.getKey());
		}

		Comparator<RavensObjectMatch> comparator = new RavensObjectMatchComparator();
		PriorityQueue<RavensObjectMatch> pq = new PriorityQueue<RavensObjectMatch>(100, comparator);

		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
				RavensObjectMatch m = new RavensObjectMatch(eA.getKey()+eB.getKey());

				ArrayList<String> atB = new ArrayList<>();
				for (Entry<String,String> eBat : eB.getValue().attributes.entrySet()) {
					atB.add(eBat.getKey());
				}

				for (Entry<String,String> eAat : eA.getValue().attributes.entrySet()) {
					String ak = eAat.getKey();
					String av = eAat.getValue();
					String bv = eB.getValue().attributes.get(ak);

					if (bv == null) {
						m.score -= 2;
						continue;
					}
					else if (ak.equals("shape")) {
						if (av.equals(bv)) m.score += 5;
						else m.score -= 5;
					}
					else if (ak.equals("size")) {
						if (av.equals(bv)) m.score += 4;
						else m.score -= 4;
					}
					else if (ak.equals("fill")) {
						if (av.equals(bv)) m.score += 3;
						else m.score -= 3;
					}
					else {
						String[] temp = av.split(",");
						if (av.equals(bv)) m.score += 2*temp.length;
						else m.score -= 1*temp.length;
					}
					atB.remove(ak);
				}
				if (!atB.isEmpty()) m.score -= 2*atB.size();

				pq.add(m);
			}
		}

		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			String a = Character.toString(m.name.charAt(0));
			String b = Character.toString(m.name.charAt(1));

			if (as.isEmpty() || bs.isEmpty()) break;
			if (!as.contains(a) || !bs.contains(b)) continue;

			B.changeObjectName(b,a);
			as.remove(a);
			bs.remove(b);
		}
		int i = 97;
		if (!bs.isEmpty()) {
			Iterator<String> it = bs.iterator();
			while(it.hasNext()) {
				String b = it.next();
				while(objNoListAB.contains(i)) {
					i++;
				}
				B.changeObjectName(b,Character.toString((char)i));
			}
		}
	}
	
	public static void makeShapes() {
		Shape tmp;

		shapes.put("circle",new Shape("circle",0,1));
		tmp = shapes.get("circle");
		tmp.addRotate("all",0);

		shapes.put("square",new Shape("square",4,45));
		tmp = shapes.get("square");
		tmp.addRotate("0,90",0);
		tmp.addRotate("90,180",0);
		tmp.addRotate("180,270",0);
		tmp.addRotate("0,180",0);
		tmp.addRotate("90,270",0);
		tmp.addRotate("0,270",0);
		tmp.addRotate("45,135",0);
		tmp.addRotate("135,225",0);
		tmp.addRotate("225,315",0);
		tmp.addRotate("45,225",0);
		tmp.addRotate("135,315",0);
		tmp.addRotate("45,315",0);
		
		shapes.put("triangle",new Shape("triangle",3,120));
		tmp = shapes.get("triangle");
		tmp.addReflect("0,180",1);
		tmp.addMirror("90,270",1);

		shapes.put("right-triangle",new Shape("right-triangle",3,360));
		tmp = shapes.get("right-triangle");
		tmp.addReflect("0,90",1);
		tmp.addReflect("180,270",1);
		tmp.addMirror("90,180",1);
		tmp.addMirror("0,270",1);

		shapes.put("Pac-Man",new Shape("Pac-Man",0,360));
		tmp = shapes.get("Pac-Man");
		tmp.addReflect("90,270",1);
		tmp.addReflect("45,315",1);
		tmp.addReflect("135,225",1);
		tmp.addMirror("0,180",1);
		tmp.addMirror("45,135",1);
		tmp.addMirror("225,315",1);
		

		shapes.put("plus",new Shape("plus",12,45));
		tmp = shapes.get("plus");
		tmp.addRotate("0,90",0);
		tmp.addRotate("90,180",0);
		tmp.addRotate("180,270",0);
		tmp.addRotate("0,180",0);
		tmp.addRotate("90,270",0);
		tmp.addRotate("0,270",0);
		tmp.addRotate("45,135",0);
		tmp.addRotate("135,225",0);
		tmp.addRotate("225,315",0);
		tmp.addRotate("45,225",0);
		tmp.addRotate("135,315",0);
		tmp.addRotate("45,315",0);
		
		shapes.put("rectangle",new Shape("rectangle",4,90));
		tmp = shapes.get("rectangle");
		tmp.addRotate("0,180",0);
		tmp.addRotate("90,270",0);
		tmp.addRotate("45,225",0);
		tmp.addRotate("135,315",0);
		tmp.addReflect("45,135",1);
		tmp.addReflect("45,315",1);
		tmp.addReflect("135,225",1);
		tmp.addReflect("225,315",1);
		tmp.addReflect("45,315",1);
		tmp.addMirror("45,135",1);
		tmp.addMirror("45,315",1);
		tmp.addMirror("135,225",1);
		tmp.addMirror("225,315",1);
		tmp.addMirror("45,315",1);
		
		shapes.put("arrow",new Shape("arrow",7,360));
		tmp = shapes.get("arrow");
		tmp.addReflect("90,270",1);
		tmp.addMirror("0,180",1);
		
		shapes.put("half-arrow",new Shape("arrow",5,360));
		tmp = shapes.get("half-arrow");
		
	}
}