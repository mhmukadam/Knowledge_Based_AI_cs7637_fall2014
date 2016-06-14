package project4;

public class ParseObject {
	public String shape;
	public double perimeter;
	public double area;
	public String fill;
	public String size;
	public double cx;
	public double cy;
	public String inside = "";
	public String leftof = "";
	public String above = "";
	public double angle;
	
	public ParseObject(String s,double pe,double a,String sz,double cx,double cy,double angle) {
		shape = s;
		perimeter = pe;
		area = a;
		fill = "yes";
		size = sz;
		this.cx = cx;
		this.cy = cy;
		this.angle = angle;
	}
	
}
