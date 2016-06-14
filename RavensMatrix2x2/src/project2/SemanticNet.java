package project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SemanticNet {
	public String name;
	public HashMap<String,SNNode> nodes;
	//	public boolean groupTranform;

	public SemanticNet(String name) {
		this.name = name;
		this.nodes = new HashMap<>();
		//		this.groupTranform = false;
	}

	public void createSemanticNet(MyRavensFigure A, MyRavensFigure B, String direction) {

		ArrayList<String> as = new ArrayList<>();
		ArrayList<String> bs = new ArrayList<>();
		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			as.add(eA.getKey());
			this.nodes.put("A"+eA.getKey(),new SNNode());
		}
		for (Entry<String,MyRavensObject> eB : B.objects.entrySet()) {
			bs.add(eB.getKey());
			this.nodes.put("B"+eB.getKey(),new SNNode());
		}

		for (Entry<String,MyRavensObject> eA : A.objects.entrySet()) {
			String objAk = eA.getKey();
			MyRavensObject objAv = eA.getValue();
			MyRavensObject objBv = B.objects.get(objAk);
			if (objBv == null) {
				// Object deleted from A to B
				for (Entry<String,String> eAat : objAv.attributes.entrySet()) {
					this.nodes.get("A"+objAk).links.put(eAat.getKey(),eAat.getValue());
				}
				this.nodes.get("A"+objAk).links.put("deleted","true");
			}
			else {
				// Matched object is compared between A and B
				ArrayList<String> atB = new ArrayList<>();
				for (Entry<String,String> eBat : objBv.attributes.entrySet()) {
					atB.add(eBat.getKey());
				}

				for (Entry<String,String> eAat : objAv.attributes.entrySet()) {
					String ak = eAat.getKey();
					String av = eAat.getValue();
					String bv = objBv.attributes.get(ak);

					//********************Attribute Comparisons**********************//

					if (bv == null) {
						// Deleted attribute from A to B
						this.nodes.get("A"+objAk).links.put(ak,av);
						continue;
					}
					else if (ak.equals("shape")) {
						if (av.equals(bv)) this.nodes.get("A"+objAk).links.put(ak,av+","+bv+",true");
						else this.nodes.get("A"+objAk).links.put(ak,av+","+bv+",false");
					}
					else if (ak.equals("size")) {
						this.nodes.get("A"+objAk).links.put(ak,Transformation.sizeChange(av,bv));
					}
					else if (ak.equals("angle")) {
						String shapeA = objAv.attributes.get("shape");
						String shapeB = objBv.attributes.get("shape");
						if (shapeA.equals(shapeB)) {
							if (direction.equals("horizontal") && Transformation.isMirrored(av,bv,shapeA) == 1) {
								this.nodes.get("A"+objAk).links.put("mirror","1");
							}
							else if (direction.equals("vertical") && Transformation.isReflected(av,bv,shapeA) == 1) {
								this.nodes.get("A"+objAk).links.put("reflect","1");
							}
							else {
								this.nodes.get("A"+objAk).links.put("rotate",Integer.toString(Transformation.isRotated(av,bv,shapeA)));
							}
						}
						else {
							this.nodes.get("A"+objAk).links.put("rotate",Integer.toString(Transformation.isRotated(av,bv,"false")));
						}
					}
					else {
						if (av.contains(",") || bv.contains(",")) {
							String tempA = "";
							String tempB = "";
							ArrayList<String> avs = new ArrayList<String>(Arrays.asList(av.split(",")));
							ArrayList<String> bvs = new ArrayList<String>(Arrays.asList(bv.split(",")));
							Iterator<String> it = avs.iterator();
							while (it.hasNext()) {
								String avsV = it.next();
								if (bvs.contains(avsV)) {
									it.remove();
									bvs.remove(avsV);
									tempA+="true,";
								}
							}
							if (!avs.isEmpty()) {
								for (String avsV : avs) {
									tempA+=avsV;
								}
							}
							if (!bvs.isEmpty()) {
								for (String bvsV : bvs) {
									tempB+=bvsV;
								}
							}
							if (!tempA.isEmpty()) this.nodes.get("A"+objAk).links.put(ak,tempA.replaceAll(",$",""));
							if (!tempB.isEmpty()) this.nodes.get("B"+objAk).links.put(ak,tempB.replaceAll(",$",""));
						}
						else {
							if (av.equals(bv)) this.nodes.get("A"+objAk).links.put(ak,"true");
							else this.nodes.get("A"+objAk).links.put(ak,"false");
						}
					}

					//**************************************************************//

					atB.remove(ak);
				}
				if (!atB.isEmpty()) {
					// Added attributes from A to B
					for (String bk : atB) {
						this.nodes.get("B"+objAk).links.put(bk,objBv.attributes.get(bk));
					}
				}
			}
			bs.remove(objAk);
		}
		if(!bs.isEmpty()) {
			// Objects added from A to B
			for (String objBk : bs) {
				MyRavensObject objBv = B.objects.get(objBk);
				for (Entry<String,String> eBat : objBv.attributes.entrySet()) {
					this.nodes.get("B"+objBk).links.put(eBat.getKey(),eBat.getValue());
				}
				this.nodes.get("B"+objBk).links.put("added","true");
			}
		}
		
//		this.printSemanticNet(); //uncomment this to print all semantic nets as they are generated
	}

	public int testSimilarity(SemanticNet sink) {

		int simScore = 0;

		for (Entry<String,SNNode> e : this.nodes.entrySet()) {
			SNNode sourceNode = e.getValue();
			SNNode sinkNode = sink.nodes.get(e.getKey());

			// Deleted node
			if (sinkNode == null) {
				// Deleted node exists in A or B, in source
				if (sourceNode.links.containsKey("added") || sourceNode.links.containsKey("deleted")) {
					simScore -= (sourceNode.links.size()-1);
				}
				else {
				// Deleted node exists in both A and B, in source (reward removal in this case)
					simScore += sourceNode.links.size();
				}
			}
			else {
				String sourceShape = sourceNode.links.get("shape");
				String sinkShape = sinkNode.links.get("shape");
				
				// Compare links
				for (Entry<String,String> e2 : sourceNode.links.entrySet()) {
					String sourceLinkk = e2.getKey();
					String sourceLink = e2.getValue();
					String sinkLink = sinkNode.links.get(sourceLinkk);

					// Deleted link
					if (sinkLink == null) {
						simScore -= 1;
					}
					else {
						if (sourceLinkk.equals("shape")) {
							if ((sourceLink.contains("true") && sinkLink.contains("true")) || (sourceLink.contains("false") && sinkLink.contains("false"))) simScore += 6;
							else simScore -= 6;
						}
						else if (sourceLinkk.equals("fill")) {
							if (sourceLink.equals(sinkLink)) simScore += 5;
							else simScore -= 5;
						}
						else if (sourceLinkk.equals("reflect") || sourceLinkk.equals("mirror")) {
							if (sourceLink.equals(sinkLink)) simScore += 4;
							else simScore -= 4;
						}
						else if (sourceLinkk.equals("rotate")) {
							if (sourceLink.equals(sinkLink)) simScore += 3;
							else if (sourceShape != null && sinkShape != null) {
								if (sourceShape.contains("circle,circle") || sinkShape.contains("circle,circle")) simScore += 3;
							}
							else simScore -= 3;
						}
						else if (sourceLinkk.equals("angle")) {
							if (sourceLink.equals(sinkLink)) simScore += 3;
							else simScore -= 3;
						}
						else if (sourceLinkk.equals("size")) {
							if (sourceLink.equals(sinkLink)) simScore += 2;
							else simScore -= 2;
						}
						else if (sourceLink.contains(",") || sinkLink.contains(",")) {
							ArrayList<String> sol = new ArrayList<String>(Arrays.asList(sourceLink.split(",")));
							ArrayList<String> skl = new ArrayList<String>(Arrays.asList(sinkLink.split(",")));
							Iterator<String> it = sol.iterator();
							while (it.hasNext()) {
								String solv = it.next();
								if (skl.contains(solv)) {
									it.remove();
									skl.remove(solv);
									simScore += 1;
								}
							}
							if (!sol.isEmpty()) {
								simScore -= sol.size();
							}
							if (!skl.isEmpty()) {
								simScore -= skl.size();
							}
						}
						else {
							if (sourceLink.equals(sinkLink)) simScore += 1;
							else simScore -= 1;
						}
						sinkNode.links.remove(sourceLinkk);
					}
				}
				// Added links
				if (!sinkNode.links.isEmpty()) {
					simScore -= sinkNode.links.size();
				}
				sink.nodes.remove(sinkNode);
			}
		}
		// Added nodes
		if (!sink.nodes.isEmpty()) {
			for (Entry<String,SNNode> e : sink.nodes.entrySet()) {
				SNNode sinkNode = e.getValue();
				// Added node exists in A or B, in source
				if (sinkNode.links.containsKey("added") || sinkNode.links.containsKey("deleted")) {
					simScore -= (sinkNode.links.size()-1);
				}
				else {
				// Added node exists in both A and B, in source (reward addition in this case)
					for (Entry<String,String> e2 : sinkNode.links.entrySet()) {
						if (e2.getValue().contains("true")) simScore += 1;
						else if (e2.getKey().equals("rotate") && e2.getValue().equals("0")) simScore += 1;
						else simScore -= 1;
					}
				}
			}
		}

		return simScore;
	}

	//print SemanticNet
	public void printSemanticNet() {
		System.out.println("******************");
		System.out.println("\t"+this.name);
		System.out.println("******************");
		for (Entry<String,SNNode> e1 : this.nodes.entrySet()) {
			String key = e1.getKey();
			System.out.println(key);
			SNNode value = e1.getValue();
			for (Entry<String,String> e2: value.links.entrySet()) {
				System.out.println(e2.getKey()+" "+e2.getValue());
			}
		}
		System.out.println();
	}
	
}