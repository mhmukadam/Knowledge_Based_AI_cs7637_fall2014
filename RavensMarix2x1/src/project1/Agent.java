package project1;

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
    public Agent() {
        
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
    	//if (!problem.getName().equals("2x1 Basic Problem 16")) return "0";
    	//***************************************************//
    	
    	//******************Current Problem******************//
    	System.out.println("************************************");
    	System.out.println("Solving | "+problem.getName());
    	
    	RavensFigure A = problem.getFigures().get("A");
    	RavensFigure B = problem.getFigures().get("B");
    	RavensFigure C = problem.getFigures().get("C");
    	
    	SemanticNet source = new SemanticNet("AB");
    	source.createSemanticNet(A,B);
    	
    	SemanticNet[] sink = new SemanticNet[7];
    	int[] simScore = new int[7];
    	int maxScore = Integer.MIN_VALUE;
    	int maxOption = 0;
    	System.out.println("Scores:");
    	for (int i=1; i<= 6; i++) {
    		sink[i] = new SemanticNet(Integer.toString(i));
    		sink[i].createSemanticNet(C,problem.getFigures().get(Integer.toString(i)));
    		simScore[i] = source.testSimilarity(sink[i]);
    		System.out.print(" | "+simScore[i]);
    		if (maxScore <= simScore[i]) {
    			maxScore = simScore[i];
    			maxOption = i;
    		}
    	}
    	System.out.println();
    	System.out.println("************************************");
    	System.out.println();
    	
        return Integer.toString(maxOption);
    }
}











