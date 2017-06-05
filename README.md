# DataflowMachine
A simple dataflow machine written in java supposed to evaluate a math expression written using polish notation.
The machine is purely an thought exercise as it involves parallel computing of a math expression using way too many system resourses when there are linear solutions to this problem. 
If you must evaluate a polish notation I warmly suggest you using the double stack as it operates with much lower memory and does not waist a lot of time building a tree, 
because as it is right now the worst part of this class is actually the time it takes to parse the expression and then build the tree.
