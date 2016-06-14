package project4;

import java.util.ArrayList;
import java.util.Arrays;
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
	public String Solve(VisualRavensProblem problem) {
		//****************Problem Restriction****************//
		//for debugging one problem at a time
		//if (!problem.getName().equals("3x3 Basic Problem 15")) return "0";
		//***************************************************//

		//******************Current Problem******************//
		System.out.println("************************************");
		System.out.println("Solving | "+problem.getName());
		int maxOption = 0;


		if (problem.getName().contains("2x")) {
			RavensFigure oldA = ParseImage.parseToRavensFigure(problem.getFigures().get("A"));
			RavensFigure oldB = ParseImage.parseToRavensFigure(problem.getFigures().get("B"));
			RavensFigure oldC = ParseImage.parseToRavensFigure(problem.getFigures().get("C"));

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
			System.out.println("Scores:");

			for (int i=1; i<= 6; i++) {
				I.add(new MyRavensFigure(ParseImage.parseToRavensFigure(problem.getFigures().get(Integer.toString(i)))));
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
		}
		else {
			//Get all figures
			RavensFigure oldA = ParseImage.parseToRavensFigure(problem.getFigures().get("A"));
			RavensFigure oldB = ParseImage.parseToRavensFigure(problem.getFigures().get("B"));
			RavensFigure oldC = ParseImage.parseToRavensFigure(problem.getFigures().get("C"));
			RavensFigure oldD = ParseImage.parseToRavensFigure(problem.getFigures().get("D"));
			RavensFigure oldE = ParseImage.parseToRavensFigure(problem.getFigures().get("E"));
			RavensFigure oldF = ParseImage.parseToRavensFigure(problem.getFigures().get("F"));
			RavensFigure oldG = ParseImage.parseToRavensFigure(problem.getFigures().get("G"));
			RavensFigure oldH = ParseImage.parseToRavensFigure(problem.getFigures().get("H"));

			//Create easy to manipulate versions
			MyRavensFigure A = new MyRavensFigure(oldA);
			MyRavensFigure B = new MyRavensFigure(oldB);
			MyRavensFigure C = new MyRavensFigure(oldC);
			MyRavensFigure D = new MyRavensFigure(oldD);
			MyRavensFigure E = new MyRavensFigure(oldE);
			MyRavensFigure F = new MyRavensFigure(oldF);
			MyRavensFigure G = new MyRavensFigure(oldG);
			MyRavensFigure H = new MyRavensFigure(oldH);

			//Find matches across all figures and adjust their names accordingly
			findMatches1_3x3(A,B);
			findMatches3x3(B,C,0);
			findMatches3x3(A,D,0);
			findMatches3x3(B,E,0);
			findMatches3x3(C,F,0);
			reCheck(F,E);
			reCheck(E,D);
			reCheck(D,F);
			findMatches3x3(D,G,0);
			findMatches3x3(E,H,0);

			//Make Semantic Nets for row 1 and 2
			SemanticNet[] sA = new SemanticNet[4];
			sA[1] = new SemanticNet("1");
			sA[1].createSemanticNet(A,B,"horizontal");
			sA[2] = new SemanticNet("2");
			sA[2].createSemanticNet(B,C,"horizontal");
			sA[3] = new SemanticNet("3");
			sA[3].createSemanticNet(C,A,"horizontal");

			SemanticNet[] sB = new SemanticNet[4];
			sB[1] = new SemanticNet("1");
			sB[1].createSemanticNet(D,E,"horizontal");
			sB[2] = new SemanticNet("2");
			sB[2].createSemanticNet(E,F,"horizontal");
			sB[3] = new SemanticNet("3");
			sB[3].createSemanticNet(F,D,"horizontal");

			//Find global transformation rule from row 1 and 2 to apply to w 3
			ArrayList<String> rule = Transformation.globalRule(sA,sB);;
			//System.out.println("Rule: "+rule);

			//Using each option find the best solution
			int[] simScore = new int[7];
			int maxScore = Integer.MIN_VALUE;
			System.out.println("Scores:");

			for (int i=1; i<= 6; i++) {
				RavensFigure oldI = ParseImage.parseToRavensFigure(problem.getFigures().get(Integer.toString(i)));
				MyRavensFigure I = new MyRavensFigure(oldI);

				//Find remaining matches and fix names
				repairI(I);
				findMatches3x3(F,I,0);
				reCheck(I,H);
				reCheck(H,G);
				reCheck(G,I);

				//Semantic Nets for row 3
				SemanticNet[] sC = new SemanticNet[4];
				sC[1] = new SemanticNet("1");
				sC[1].createSemanticNet(G,H,"horizontal");
				sC[2] = new SemanticNet("2");
				sC[2].createSemanticNet(H,I,"horizontal");
				sC[3] = new SemanticNet("3");
				sC[3].createSemanticNet(I,G,"horizontal");

				//Compare 2nd row to 3rd row using rule
				for (String r: rule) {
					int r1 = Integer.parseInt(Character.toString(r.charAt(0)));
					int r2 = Integer.parseInt(Character.toString(r.charAt(1)));
					simScore[i] += sB[r1].testSimilarity(sC[r2]);
				}

				//Update max score
				System.out.print(" | "+simScore[i]);
				if (maxScore <= simScore[i]) {
					maxScore = simScore[i];
					maxOption = i;
				}
			}
			System.out.println();
			System.out.println("************************************");
			System.out.println();
			
		}
		// Reset Variables
		objNo = 97;
		objNoListAB = new ArrayList<>();
		objNoListAC = new ArrayList<>();

		return Integer.toString(maxOption);
	}

	public static void printPQ(PriorityQueue<RavensObjectMatch> pq) {
		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			System.out.println(m.name+" : "+m.score);
		}
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
						if (av.contains(",") || bv.contains(",")) {
							ArrayList<String> avs = new ArrayList<String>(Arrays.asList(av.split(",")));
							ArrayList<String> bvs = new ArrayList<String>(Arrays.asList(bv.split(",")));
							Iterator<String> it = avs.iterator();
							while (it.hasNext()) {
								String avsV = it.next();
								if (bvs.contains(avsV)) {
									it.remove();
									bvs.remove(avsV);
									m.score += 1;
								}
							}
							if (!avs.isEmpty()) m.score -= avs.size();
							if (!bvs.isEmpty()) m.score -= bvs.size();
						}
						else {
							if (av.equals(bv)) m.score += 1;
							else m.score -= 1;
						}
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
						if (av.contains(",") || bv.contains(",")) {
							ArrayList<String> avs = new ArrayList<String>(Arrays.asList(av.split(",")));
							ArrayList<String> bvs = new ArrayList<String>(Arrays.asList(bv.split(",")));
							Iterator<String> it = avs.iterator();
							while (it.hasNext()) {
								String avsV = it.next();
								if (bvs.contains(avsV)) {
									it.remove();
									bvs.remove(avsV);
									m.score += 1;
								}
							}
							if (!avs.isEmpty()) m.score -= avs.size();
							if (!bvs.isEmpty()) m.score -= bvs.size();
						}
						else {
							if (av.equals(bv)) m.score += 1;
							else m.score -= 1;
						}
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

	public static void findMatches1_3x3(MyRavensFigure A, MyRavensFigure B) {

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
						if (av.contains(",") || bv.contains(",")) {
							ArrayList<String> avs = new ArrayList<String>(Arrays.asList(av.split(",")));
							ArrayList<String> bvs = new ArrayList<String>(Arrays.asList(bv.split(",")));
							Iterator<String> it = avs.iterator();
							while (it.hasNext()) {
								String avsV = it.next();
								if (bvs.contains(avsV)) {
									it.remove();
									bvs.remove(avsV);
									m.score += 1;
								}
							}
							if (!avs.isEmpty()) m.score -= avs.size();
							if (!bvs.isEmpty()) m.score -= bvs.size();
						}
						else {
							if (av.equals(bv)) m.score += 1;
							else m.score -= 1;
						}
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

			A.changeObjectName(a,Character.toString((char)objNo));
			B.changeObjectName(b,Character.toString((char)objNo));
			objNo++;
			objNoListAB.add(objNo);
			objNoListAC.add(objNo);
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

	public static void findMatches3x3(MyRavensFigure A, MyRavensFigure B, int p) {

		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			as.add(eA.getKey());
		}
		for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
			bs.add(eB.getKey());
		}

		if (as.isEmpty() || bs.isEmpty()) return;
		else matches3x3rec(A,B,as,bs,p);

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

	public static void matches3x3rec(MyRavensFigure A, MyRavensFigure B, ArrayList<String> as, ArrayList<String> bs, int p) {
		Comparator<RavensObjectMatch> comparator = new RavensObjectMatchComparator();
		PriorityQueue<RavensObjectMatch> pq = new PriorityQueue<RavensObjectMatch>(100, comparator);

		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {

			if (B.objects.containsKey(eA.getKey())) continue;

			for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
				RavensObjectMatch m = new RavensObjectMatch(eA.getKey()+eB.getKey());

				if (A.objects.containsKey(eB.getKey())) continue;

				ArrayList<String> atB = new ArrayList<>();
				for (Entry<String,String> eBat : eB.getValue().attributes.entrySet()) {
					atB.add(eBat.getKey());
				}

				int perfectMatch = 1;

				for (Entry<String,String> eAat : eA.getValue().attributes.entrySet()) {
					String ak = eAat.getKey();
					String av = eAat.getValue();
					String bv = eB.getValue().attributes.get(ak);

					if (bv == null) {
						m.score -= 2;
						perfectMatch = 0;
						continue;
					}
					else if (ak.equals("shape")) {
						if (av.equals(bv)) m.score += 5;
						else {
							perfectMatch = 0;
							m.score -= 5;
						}
					}
					else if (ak.equals("size")) {
						if (av.equals(bv)) m.score += 4;
						else {
							perfectMatch = 0;
							m.score -= 4;
						}
					}
					else if (ak.equals("fill")) {
						if (av.equals(bv)) m.score += 3;
						else {
							perfectMatch = 0;
							m.score -= 3;
						}
					}
					else {
						if (av.contains(",") || bv.contains(",")) {
							ArrayList<String> avs = new ArrayList<String>(Arrays.asList(av.split(",")));
							ArrayList<String> bvs = new ArrayList<String>(Arrays.asList(bv.split(",")));
							Iterator<String> it = avs.iterator();
							while (it.hasNext()) {
								String avsV = it.next();
								if (bvs.contains(avsV)) {
									it.remove();
									bvs.remove(avsV);
									m.score += 2;
								}
							}
							if (!avs.isEmpty()) {
								perfectMatch = 0;
								m.score -= 1*avs.size();
							}
							if (!bvs.isEmpty()) {
								perfectMatch = 0;
								m.score -= 1*bvs.size();
							}
						}
						else {
							if (av.equals(bv)) m.score += 2;
							else {
								perfectMatch = 0;
								m.score -= 1;
							}
						}	
					}
					atB.remove(ak);
				}
				if (!atB.isEmpty()) {
					perfectMatch = 0;
					m.score -= 2*atB.size();
				}

				if (perfectMatch == 1) {
					String a = eA.getKey();
					String b = eB.getKey();

					if (as.isEmpty() || bs.isEmpty()) continue;
					if (!as.contains(a) || !bs.contains(b)) continue;

					m.score += 500;
				}

				pq.add(m);
			}
		}

		String a = null;
		String b = null;

		while (pq.size() != 0) {
			RavensObjectMatch m = pq.remove();
			a = Character.toString(m.name.charAt(0));
			b = Character.toString(m.name.charAt(1));

			if (as.isEmpty() || bs.isEmpty()) return;
			if (!as.contains(a) || !bs.contains(b)) continue;
			else break;
		}

		B.changeObjectName(b,a);
		as.remove(a);
		bs.remove(b);

		if (p==1) {
			System.out.println("as: "+as);
			System.out.println("bs: "+bs);
		}

		if (as.isEmpty() || bs.isEmpty()) return;
		else matches3x3rec(A,B,as,bs,p);

	}

	public static void reCheck(MyRavensFigure A, MyRavensFigure B) {

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

			if (B.objects.containsKey(eA.getKey())) {
				as.remove(eA.getKey());
				bs.remove(eA.getKey());
				continue;
			}

			for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {

				if (A.objects.containsKey(eB.getKey())) continue;

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
						if (av.contains(",") || bv.contains(",")) {
							ArrayList<String> avs = new ArrayList<String>(Arrays.asList(av.split(",")));
							ArrayList<String> bvs = new ArrayList<String>(Arrays.asList(bv.split(",")));
							Iterator<String> it = avs.iterator();
							while (it.hasNext()) {
								String avsV = it.next();
								if (bvs.contains(avsV)) {
									it.remove();
									bvs.remove(avsV);
									m.score += 1;
								}
							}
							if (!avs.isEmpty()) m.score -= avs.size();
							if (!bvs.isEmpty()) m.score -= bvs.size();
						}
						else {
							if (av.equals(bv)) m.score += 1;
							else m.score -= 1;
						}
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

	}

	public static void repairI(MyRavensFigure I) {
		int i = 65;
		ArrayList<String> repairs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eI : I.objects.entrySet()) {
			String o = eI.getKey();
			int c = (int) o.charAt(0);
			if (c >= 97 && c <= 122) {
				repairs.add(o+Character.toString((char)i));
				i++;
			}
		}
		if (!repairs.isEmpty()) {
			Iterator<String> it = repairs.iterator();
			while(it.hasNext()) {
				String s = it.next();
				I.changeObjectName(Character.toString((char)s.charAt(0)),Character.toString((char)s.charAt(1)));
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