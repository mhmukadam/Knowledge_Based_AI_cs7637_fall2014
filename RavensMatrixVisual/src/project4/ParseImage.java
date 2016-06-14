package project4;

import org.opencv.core.*;
import org.opencv.highgui.*;
import org.opencv.imgproc.*;

import java.util.*;
import java.util.Map.Entry;

public class ParseImage {
	
	public static RavensFigure parseToRavensFigure(VisualRavensFigure vA) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//Read Image,convert to gray and do Canny edge detection
		Mat src = Highgui.imread(vA.getPath(),1);
		Mat gray = new Mat();
		Imgproc.cvtColor(src,gray,Imgproc.COLOR_BGR2GRAY);
		Mat bw = new Mat();
		Imgproc.Canny(gray,bw,0,255,3,false);
		
		//Generate hierarchy of contours
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat hie = new Mat();
		Imgproc.findContours(bw.clone(),contours,hie,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
		
		MatOfPoint2f approx = new MatOfPoint2f();
		MatOfPoint2f curr = new MatOfPoint2f();
		
		//Create a list of potential objects
		HashMap<String,ParseObject> objs = new HashMap<>();
		int objName = 65;
		for (int i = 0; i<contours.size(); i++)
		{
			//make an approximate contour
			contours.get(i).convertTo(curr,CvType.CV_32FC2);
		    Imgproc.approxPolyDP(curr,approx,1.1,true);
		    int sides = (int)approx.size().height;
		    String shape = ParseImage.getShape(sides);
		    double perimeter = Imgproc.arcLength(curr,true);
		    double area = Imgproc.contourArea(contours.get(i));
		    String size = ParseImage.getSize(area);
		    
		    Rect rect = Imgproc.boundingRect(contours.get(i));
		    double cx = rect.x + rect.width/2;
		    double cy = rect.y + rect.height/2;
		    
		    double angle;
		    if (curr.size().height >= 5) {
		    	RotatedRect rr = Imgproc.fitEllipse(curr);
		    	angle = rr.angle;
		    }
		    else
		    	angle = 0;
		    
		    //Get rid of repeated contours
		    if (i > 1 && !objs.isEmpty()) {
		    	ParseObject temp = objs.get(Character.toString((char)(objName-1)));
		    	if (temp != null && perimeter/temp.perimeter>0.85 && Math.abs(cx-temp.cx)<5 && Math.abs(cy-temp.cy)<5) {
		    		i++;
		    		temp.fill = "no";
		    		continue;
		    	}
		    }
		    
		    objs.put(Character.toString((char)objName),new ParseObject(shape,perimeter,area,size,cx,cy,angle));
		    objName++;
		    //System.out.println("i: "+i+", "+sides+", "+String.format("%.2f",perimeter)+", "+area+", "+cx+","+cy);
		    //System.out.println("----------------------------------------");
		    i++;
		}
		
		//Generate location attributes for objects
		if (objs.size() > 1) {
			for (Entry<String,ParseObject> e1 : objs.entrySet()) {
				ParseObject o1 = e1.getValue();
				double x1 = o1.cx;
				double y1 = o1.cy;
				for (Entry<String,ParseObject> e2 : objs.entrySet()) {
					ParseObject o2 = e2.getValue();
					double x2 = o2.cx;
					double y2 = o2.cy;
					String n2 = e2.getKey()+",";
					
					if (Math.abs(x1-x2)<2 && Math.abs(y1-y2)<2 && o1.area<o2.area)
						o1.inside += n2;
					if (x1 < x2 && Math.abs(x1-x2) > 5)
						o1.leftof += n2;
					if (y1 < y2 && Math.abs(y1-y2) > 5)
						o1.above += n2;
				}
			}
		}
		
		//Create RavensFigure to return with the collected objects
		RavensFigure A = new RavensFigure(vA.getName());
		for (Entry<String,ParseObject> e1 : objs.entrySet()) {
			ParseObject o1 = e1.getValue();
			
			RavensObject temp = new RavensObject(e1.getKey());
			ArrayList<RavensAttribute> ats = temp.getAttributes();
			
			RavensAttribute at1 = new RavensAttribute("shape",o1.shape);
			ats.add(at1);
			RavensAttribute at2 = new RavensAttribute("fill",o1.fill);
			ats.add(at2);
			RavensAttribute at3 = new RavensAttribute("size",o1.size);
			ats.add(at3);
			if (!o1.inside.isEmpty()) {
				RavensAttribute at4 = new RavensAttribute("inside",o1.inside.replaceAll(",$",""));
				ats.add(at4);
			}
			if (!o1.above.isEmpty()) {
				RavensAttribute at5 = new RavensAttribute("above",o1.above.replaceAll(",$",""));
				ats.add(at5);
			}
			if (!o1.leftof.isEmpty()) {
				RavensAttribute at6 = new RavensAttribute("left-of",o1.leftof.replaceAll(",$",""));
				ats.add(at6);
			}
			//RavensAttribute at7 = new RavensAttribute("angle",Integer.toString((int)o1.angle));
			//ats.add(at7);
			
			A.getObjects().add(temp);
		}

		return A;
	}
	
	public static String getShape(int sides) {
		if (sides == 3)
			return "triangle";
		else if (sides == 4)
			return "square";
		else if (sides == 5)
			return "pentagon";
		else if (sides == 6)
			return "hexagon";
		else if (sides == 7)
			return "heptagon";
		else if (sides == 8)
			return "octagon";
		else if (sides == 12)
			return "plus";
		else if (sides == 16 && sides == 26)
			return "circle";
		else if (sides >= 21 && sides <= 25)
			return "pac-man";
		else
			return "unknown";
	}
	
	public static String getSize(double area) {
		double r = area/(184*184);
		if (r > 0.7)
			return "very-large";
		else if (r > 0.45)
			return "large";
		else if (r > 0.25)
			return "medium";
		else if (r > 0.12)
			return "small";
		else
			return "very-small";
	}
	
}
