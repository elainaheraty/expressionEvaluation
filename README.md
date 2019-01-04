# expressionEvaluation
Here are some sample expressions of the kind my program will evaluate:

   3
   
   Xyz
   
   3-4*5
   
   a-(b+A[B[2]])*d+3
   
   A[2*(a+b)]
   
   (varx + vary*varz[(vara+varb[(a+b)*33])])/55
   
The expressions will be restricted to the following components:
Integer constants
Simple (non-array) variables with integer values
Arrays of integers, indexed with a constant or a subexpression
Addition, subtraction, multiplication, and division operators, i.e. '+','-','*','/'
Parenthesized subexpressions

The Expressions class consists of methods for various steps of the evaluation process:
1) makeVariableLists - This method populates the vars and arrays lists with Variable and Array objects, respectively, for the simple variable and arrays that appear in the expression. 
2) loadVariableValues - This method reads values for all simple variables and arrays arrays from a file, into the Variable and Array objects stored in the vars and arrays array lists. 
3) evaluate - This method evaluates the expression.

