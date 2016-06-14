package project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class SemanticNet {
	public String name;
	public HashMap<String,SNNode> nodes;
	
	public SemanticNet(String name) {
		this.name = name;
		nodes = new HashMap<>();
	}
	
	public void createSemanticNet(RavensFigure A, RavensFigure B) {
		int angleHere = 0;
		
		//create nodes
		for (RavensObject objA : A.getObjects()) {
			this.nodes.put(objA.getName()+"A", new SNNode());
		}
		ArrayList<RavensObject> objInB = new ArrayList<>();
		for (RavensObject objB : B.getObjects()) {
			this.nodes.put(objB.getName()+"B", new SNNode());
			objInB.add(objB);
		}
		
		//create links and add them to respective nodes
		//compare objects
		for (RavensObject objA : A.getObjects()) {
			int foundObj = 0;
			for (RavensObject objB : objInB) {
				
				if (objA.getName().equals(objB.getName())) { 
					
					//collect all attributes in objB
					ArrayList<RavensAttribute> attInB = new ArrayList<>();
					for (RavensAttribute atB : objB.getAttributes()) {
						attInB.add(atB);
					}
					
					//compare attributes
					for (RavensAttribute atA : objA.getAttributes()) { 
						int foundAtt = 0;
						//compare if found
						for (RavensAttribute atB : attInB) {
							if (atA.getName().equals(atB.getName())) {
								
								if (atA.getName().equals("fill")) {//fill
									this.nodes.get(objA.getName()+"A").links.put(atA.getName(),atA.getValue()+atB.getValue());
								}
								//rotation
								else if (atA.getValue().matches("[0-9]+")) {
									angleHere = 1;
									String temp;
									temp = Integer.toString(Integer.parseInt(atB.getValue()) - Integer.parseInt(atA.getValue()));
									this.nodes.get(objA.getName()+"A").links.put(atA.getName(),temp);
								}
								else {
									String[] tempA = atA.getValue().split(",");
									String[] tempB = atB.getValue().split(",");
									
									//positional
									if (tempA[0].length() == 1) {
										for (int j=0; j < tempA.length; j++) {
											this.nodes.get(objA.getName()+"A").links.put(atA.getName()+j,tempA[j]);
										}
										for (int j=0; j < tempB.length; j++) {
											this.nodes.get(objB.getName()+"B").links.put(atB.getName()+j,tempB[j]);
										}
									}
									else {
										//default
										for (int j=0; j < Math.min(tempA.length,tempB.length); j++) {
											//true
											if (tempA[j].equals(tempB[j])) {
												this.nodes.get(objA.getName()+"A").links.put(atA.getName()+j,"true");
												//add bonus link to compensate for rotations on a circle
												if (atA.getValue().equals("circle")) {
													this.nodes.get(objA.getName()+"A").links.put("bonusLink","true");
												}
											}
											//false
											else {
												this.nodes.get(objA.getName()+"A").links.put(atA.getName()+j,"false");
											}
										}
									}	
								}
								attInB.remove(atB);	//done with this attribute
								foundAtt = 1;
								break;
							}
						}
						//was not found
						if (foundAtt == 0) { //removed attribute
							this.nodes.get(objA.getName()+"A").links.put(atA.getName(),atA.getValue());
						}
					}
					if (!attInB.isEmpty()) { //handle added attributes
						for (RavensAttribute atB : attInB) {
							this.nodes.get(objB.getName()+"B").links.put(atB.getName(),atB.getValue());
						}
					}
					//handle not needed bonus link if angle not present
					if (angleHere == 0) {
						if (this.nodes.get(objA.getName()+"A").links.get("bonusLink") != null)
							this.nodes.get(objA.getName()+"A").links.remove("bonusLink");
					}
					objInB.remove(objB); //done with this object
					foundObj = 1;
					break;
				}
			}
			//was not found
			if (foundObj == 0) { //deleted object
				this.nodes.get(objA.getName()+"A").links.put("deleted","true");
			}
		}
		if (!objInB.isEmpty()) { //handle added objects
			for (RavensObject objB : objInB) {
				this.nodes.get(objB.getName()+"B").links.put("added","true");
			}
		}
		//this.printSemanticNet(); //uncomment this to print all semantic nets as they are generated
	}
	
	public int testSimilarity(SemanticNet sink) {
		String ang = "0";
		int simScore = 0;
	
		for (Entry<String,SNNode> e : this.nodes.entrySet()) {
			SNNode sourceNode = e.getValue();
			SNNode sinkNode = sink.nodes.get(e.getKey());
			//Deleted node
			if (sinkNode == null) {
				simScore -= 10;
			}
			else {
				simScore += 10;
				for (Entry<String,String> sourceLink : sourceNode.links.entrySet()) {
			    	String sinkLink = sinkNode.links.get(sourceLink.getKey());
					//Deleted link
			    	if (sinkLink == null && !sourceLink.getKey().equals("bonusLink")) {
			    		simScore -= 1;
			    	}
			    	else {
			    		//save angle
			    		if (sourceLink.getKey().equals("angle")) ang = Integer.toString(Math.abs(Integer.parseInt(sinkLink)));
			    		
			    		//compare links
			    		if (sourceLink.getValue().equals(sinkLink)) simScore += 1;
			    		else if (sourceLink.getKey().equals("angle")) { //absolute value and 360-angle check
			    			if (Math.abs(Integer.parseInt(sourceLink.getValue())) == Math.abs(Integer.parseInt(sinkLink))) simScore += 1;
			    			else if (sourceLink.getValue().equals(Integer.toString(360-Integer.parseInt(sinkLink)))) simScore += 1;
			    			else simScore -= 1;
			    		}
			    		else simScore -= 1;
			    		//remove tallied link
			    		sinkNode.links.remove(sourceLink.getKey());
			    	}
			    }
				//added links
				if (!sinkNode.links.isEmpty()) {
					for (Entry<String,String> sinkLink : sinkNode.links.entrySet()) {
						if (sinkLink.getKey().replaceAll("[0-9]","").equals("vertical-flip") && ang.equals("180")) {
							if (sinkLink.getValue().equals("false")) simScore += 1;
							else simScore -= 1;
						}
						else if (sinkLink.getKey().equals("bonusLink")) simScore += 2;
						else simScore -= 1;
					}
				}
				//remove tallied node
				sink.nodes.remove(e.getKey());
			}
		}
		//Added nodes
		if (!sink.nodes.isEmpty()) {
			for (SNNode node : sink.nodes.values()) {
				simScore -= 10;
				for (String link : node.links.keySet()) {
					simScore -= 1;
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