package project4;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class MyRavensFigure {
	public HashMap<String,MyRavensObject> objects;
	
	public MyRavensFigure(RavensFigure A) {
		this.objects = new HashMap<>();
		for (RavensObject objA : A.getObjects()) {
			this.objects.put(objA.getName(),new MyRavensObject(objA));
		}
	}
	
	public void printMyRavensFigure() {
		System.out.println("******************");
		System.out.println("\t RavensFigure");
		System.out.println("******************");
		for (Entry<String,MyRavensObject> e : this.objects.entrySet()) {
			System.out.println(e.getKey());
			for (Entry<String,String> e2 : e.getValue().attributes.entrySet()) {
				System.out.println(e2.getKey()+" : "+e2.getValue());
			}
		}
		
	}
	
	public void changeObjectName(String o,String n) {
		MyRavensObject no = this.objects.get(o);
		this.objects.remove(o);
		this.objects.put(n,no);
		
		HashMap<String,HashMap<String,String>> addLater = new HashMap<>();
		//delete and store changes
		for (Entry<String,MyRavensObject> e : this.objects.entrySet()) {
			Iterator<Entry<String,String>> it = e.getValue().attributes.entrySet().iterator();
			addLater.put(e.getKey(),new HashMap<String,String>());
			while(it.hasNext()) {
				Entry<String,String> e2 = (Entry<String,String>)it.next();
			    String k = e2.getKey();
			    String s = e2.getValue();
			    if (s.equals(o) || (s.contains(",") && s.contains(o)) ) {
			    	it.remove();
			    	s = s.replace(o,n);
			    	addLater.get(e.getKey()).put(k,s);
			    }
			}
		}
		//add changes
		for (Entry<String,HashMap<String,String>> e : addLater.entrySet()) {
			for (Entry<String,String> e2 : e.getValue().entrySet()) {
				this.objects.get(e.getKey()).attributes.put(e2.getKey(),e2.getValue());
			}
		}
	}
	
}