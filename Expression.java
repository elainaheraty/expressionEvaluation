package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	// get all the variables
    	int k = 0;
    	String [] tokens = expr.split("[^a-zA-Z\\[]+");
    	for (int i=0; i<tokens.length; i++) {
    	  	if(tokens[i].length() > 0) {
    	  	    if(tokens[i].contains("[")) {
    	  			// array or array and variables 
    	  	       //String [] arrayToken = tokens[i].split("\\[");
    	  	    	StringBuilder sdrToken = new StringBuilder("");
    	  	    	for (int j=0; j<tokens[i].length(); j++) {
    	  	    	   if(tokens[i].charAt(j)== '[') {    	  	    		   
    	  	    	  		Array arr = new Array(sdrToken.toString());
    		    			if(arrays.isEmpty() || arrays.indexOf(arr) == -1) {
    		    				arrays.add(k++, arr);
    	  	   	   				//System.out.println("array " + arrays.toString());
    		    			}
    			            sdrToken.setLength(0);    							    					
    	  	   	    	}
    	  	    	   else {
    	  	    		 sdrToken.append(tokens[i].charAt(j));  
    	  	    	   }
    	  	       }
    	  	    	if(sdrToken.length() > 0) {
    	    			// variable
    	    			// make sure it was not added before
        				Variable var = new Variable(sdrToken.toString());
    	    			if(vars.isEmpty() || vars.indexOf(var) == -1) {
    	    				vars.add(var);
    	    				//System.out.println("variable " + vars.toString());
    	    			}    	  	    		
    	  	    	}
   	  	    	
	    		}
	    		else {
	    			// variable
	    			// make sure it was not added before
    				Variable var = new Variable(tokens[i]);
	    			if(vars.isEmpty() || vars.indexOf(var) == -1) {
	    				vars.add(var);
	    				//System.out.println("variable " + vars.toString());
	    			}
	    		}
    	  	}
	    }
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	int i = 0;
    	expr.trim();
        Stack<Character> oprStack = new Stack<>();
        Stack<Float> opdStack = new Stack<>();
        Stack<String> arStack = new Stack<>();

    	StringBuffer sbrToken = new StringBuffer("");
		float fValue = 0;
    	while (i < expr.length()) {
    		switch(expr.charAt(i)) {
    			case '(':
    				// need to evaluate the expression in the brackets
    				oprStack.push(expr.charAt(i));
    				break;
    			case ')':
    				// found closing bracket
					while(!oprStack.isEmpty() && !opdStack.isEmpty() && (oprStack.peek() != '(')) {   				
						calculate(oprStack, opdStack);
					}
    				if(oprStack.peek() == '(') {
	   	   				oprStack.pop();
    				}
    			break;
    			case '[':
    				// need to evaluate the index find everything up to closing match closing ]
    				arStack.push(sbrToken.toString());
		            sbrToken.setLength(0);    							    					
    				oprStack.push(expr.charAt(i));
    			break;
    			case ']':
    				// found closing ]
					while(!oprStack.isEmpty() && !opdStack.isEmpty() && (oprStack.peek() != '[')) {   				
						calculate(oprStack, opdStack);
					}
    				if(oprStack.peek() == '[') {
	   	   				oprStack.pop();
    				}
    				int nIndex = opdStack.pop().intValue();
     				Iterator<Array> itr = arrays.iterator();
     				while (itr.hasNext()) {
     					Array arr = itr.next();
     					if(arr.name.equals(arStack.peek())) {
     						opdStack.push((float) arr.values[nIndex]);
         				    //System.out.println("array=" + arr.name + " index=" + nIndex + " value=" + opdStack.peek());
         				    arStack.pop();
         				    break;
     					}
     				} 
     				
     				
     				//arrays.
    				
    			break;
    			case ' ':
    				break;
    			case '+':
     			case '-':
    			case '*':
    			case '/':
    				while(!oprStack.isEmpty() && (oprStack.peek() != '(') && (oprStack.peek() != '[') && isLowerPrecedence(expr.charAt(i), oprStack.peek())){
						calculate(oprStack, opdStack);						
					}
					oprStack.push(expr.charAt(i));    						
    				break;
    			default:
	    			if((expr.charAt(i) >= 'a' &&  expr.charAt(i) <= 'z') || (expr.charAt(i) >= 'A' &&  expr.charAt(i) <= 'Z')) {
	    				// collect variable name
	    				sbrToken.append(expr.charAt(i));
	    				if(i + 1  < expr.length()) {
	    					/*
	    					if(expr.charAt(i +1) == '[') {
	    						// variable is an array
	    						
	    					}
	    					else */ if (expr.charAt(i +1) == '+' ||
	    							 expr.charAt(i +1) == '-' ||
	    							 expr.charAt(i +1) == '*' ||
	    							 expr.charAt(i +1) == '/' ||
	    							 expr.charAt(i +1) == ')' ||
	    							 expr.charAt(i +1) == ']' ||
	    							 expr.charAt(i +1) == ' '){
	    						// variable name collected completely
	    						// convert integer value
		    		            Variable var = new Variable(sbrToken.toString());
		    		            int varIndex = vars.indexOf(var);
		    		            fValue = vars.get(varIndex).value;
			    				// Add operand to opdStack
			    				opdStack.push(fValue);
			    				//System.out.println("variable=" + sbrToken.toString() + " value=" + fValue);
		    		            sbrToken.setLength(0);
	    						
	    					} 
	    					
	    				}
	    				else {
	    					// the last variable of the expression 
	    					// convert to integer value;
	    		            Variable var = new Variable(sbrToken.toString());
	    		            int varIndex = vars.indexOf(var);
	    		            fValue = vars.get(varIndex).value;
		    				// Add operand to opdStack
		    				opdStack.push(fValue);
		    				//System.out.println("variable=" + sbrToken.toString() + " value=" + fValue);
	    		            sbrToken.setLength(0);    							    					
	    				}
	    				
	    				
	    			}
	    			else if(expr.charAt(i) >= '0' &&  expr.charAt(i) <= '9') {
	    				// collect all the digits of the constant 	    				bConst = true;
	    				sbrToken.append(expr.charAt(i));
	    				if(i + 1  < expr.length()) {
	    					if ( expr.charAt(i +1) == '+' ||
								 expr.charAt(i +1) == '-' ||
								 expr.charAt(i +1) == '*' ||
								 expr.charAt(i +1) == '/' ||
								 expr.charAt(i +1) == ')' ||
								 expr.charAt(i +1) == ']' ||
								 expr.charAt(i +1) == ' '){
	    						// all the digits are collect convert from string to integer
		    					fValue = Integer.parseInt(sbrToken.toString());
			    				// Add operand to opdStack
			    				opdStack.push(fValue);
			    				//System.out.println("constant=" + fValue);
		    					sbrToken.setLength(0);
	    						
	    					} 
	    					
	    				}
	    				else {
	    					// last constant of the expression convert to integer
	    					fValue = Float.parseFloat(sbrToken.toString());
		    				// Add operand to opdStack
		    				opdStack.push(fValue);
		    				//System.out.println("constant=" + fValue);
	    					sbrToken.setLength(0);
	    				}

	    			}	    				
	    			break;
    		}
    		i++;
    		
    	}
    	Float fResult = Float.valueOf(0);    	
		if(i == expr.length()) {
			while(oprStack.size() > 0 &&  opdStack.size() > 1) {
				calculate(oprStack, opdStack);
			}
			if(opdStack.size() > 0) {
				fResult = opdStack.pop(); 
				//System.out.println("Result: " + fResult.toString());
				
			}
			
		}

    	return fResult.floatValue();
    }
    private static boolean 
    isLowerPrecedence(char cFirst, char cSecond) {
    	if((cFirst == '*' || cFirst == '/') && (cSecond == '+' || cSecond == '-')){
    		return false;
    	}
    	return true;
    }
    private static void calculate(Stack<Character> oprStack, Stack<Float> opdStack) {
		Float fResult = Float.valueOf(0);		
		if(oprStack.size() > 0 &&  opdStack.size() > 1) {
	    	Float fVar1 = opdStack.pop().floatValue();
			Float fVar2 = opdStack.pop().floatValue();
			switch(oprStack.pop()) {
			case '+':
				fResult = fVar2 + fVar1;
				break;
			case '-':
				fResult = fVar2 - fVar1;
				break;
			case '*':
				fResult = fVar2 * fVar1;
				break;
			case '/':
				fResult = fVar2 / fVar1;
				break;
			}
			opdStack.push(fResult);
	  		//System.out.println("Result: " + fResult.toString());
		}
		else if(opdStack.size() > 0){
	    	fResult = opdStack.pop();
			opdStack.push(opdStack.pop().floatValue());
	  		//System.out.println("Result: " + fResult.toString());
			
		}

    }
    	
}
