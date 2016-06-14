package project2;

import java.util.HashMap;

public class MyRavensObject {
	public HashMap<String,String> attributes;
	
	public MyRavensObject(RavensObject objA) {
		this.attributes = new HashMap<>();
		for (RavensAttribute attA : objA.getAttributes()) {
			String k = new String(attA.getName());
			String v = new String(attA.getValue());
			this.attributes.put(k,v);
		}
	}
}