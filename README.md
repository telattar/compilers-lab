# compilers-lab
Weekly compilers lab assignments, coded in java.

## RegExToNfa.java
This class implements a converter from a regular expression to a non-deterministic finite automaton (NFA) using Thompson's construction algorithm.

### Class Structure

- **Instance Variables:**
  - `alphabet`: Stores the alphabet used in the regular expression.
  - `finalResult`: Represents the final operator resulting from the NFA construction.
  - `acceptedStates`: An ArrayList storing the accepted states of the NFA.

- **Methods:**

  - `isAlphabet(String alpha, char input)`: Checks if a character is in the alphabet.
  - `getAcceptedStates(Operator a)`: Returns a formatted string representation of the accepted states of the NFA.
  - `getSortedTransitions(Operator a)`: Returns a formatted string representation of the sorted transitions of the NFA.

- **Constructor:**
Constructs an NFA from a regular expression. No helper functions used and the whole process is done within the constructor. Check code comments for more details.

- **Method `toString()`**
  - Returns a formatted string representation of the NFA.

- **Example:**

```java
public static void main(String[] args) {
    // Example regular expression
    String regex = "a|b*";

    // Create an instance of RegExToNfa
    RegExToNfa nfaConverter = new RegExToNfa(regex);

    // Print the NFA representation
    System.out.println(nfaConverter);
}
```

## NfaToDfa.java
This Java program converts a Non-Deterministic Finite Automaton (NFA) to a Deterministic Finite Automaton (DFA). It provides functionalities for epsilon closure, epsilon closure merging, and DFA transition generation.
Takes a regular expression as an input string. This code's instance variables include a lot of helper variables. To prevent confusion, I will not be listing them.

- **Constructor:**
    - Initializes the NFA to DFA converter.
    - Takes a formatted string representation of the NFA as input and performs the conversion.

- **Methods:**
  - `epsilonClosure(ArrayList<Transition> t)`: Computes the epsilon closure for each state in the NFA. It returns a TreeMap where each key represents a state, and the corresponding value is an ArrayList containing all states reachable from the key state through epsilon transitions.
  - `epsilonClosureMerged(TreeMap<Integer, ArrayList<Integer>> input)`: Merges the epsilon closures of states that share common epsilon transitions. It iteratively merges states until no more merges can be made.
  - `getTransitionsDFA(TreeMap<Integer, ArrayList<Integer>> epsilonClosure, ArrayList<Transition> transits, int start)`: Generates the transitions for the equivalent DFA based on the epsilon closures and transitions of the NFA. It constructs the DFA transitions using the epsilon closures and transitions of the NFA, starting from the specified start state.
  - `sortArray(ArrayList<ArrayList<Integer>> array)`: Sorts an ArrayList of ArrayLists of integers lexicographically.
  - `sortDFA()`: Sorts the DFA transitions lexicographically based on the start state, transition symbol, and final state.
  - `NfaToDfa(String input)`: Constructor method that initializes the NFA to DFA converter. It takes a formatted string representation of the NFA as input and performs the conversion.
  - `toString()`: Overrides the toString() method to generate a formatted string representation of the DFA. The string representation follows the format specified in the task description and includes states, transitions, starting states, and accept states.
 
- **Example:**
```java
public static void main(String[] args) {
        String nfaInput = "1#0;1;2;3#0,a,1;0,a,2;1,b,3;2,b,3#1#2";
                NfaToDfa nfaToDfa = new NfaToDfa(nfaInput);
        
        // Convert NFA to DFA and print the formatted DFA string
        System.out.println(nfaToDfa.toString());
    }
 ```

 
## CfgEpsUnitElim.java
Eliminating epsilon and unit rules from a context-free grammar (CFG) simplifies parsing algorithms by avoiding ambiguity in language recognition.
The program eliminates epsilon and unit rules from a given Context Free Grammar (CFG). 

### Class Structure

The `CfgEpsUnitElim` class consists of the following elements:

- **Instance Variables:**
  - `contextGrammar`: A Hashtable storing the production rules of the CFG.
  - `vars`: A string representing the variables of the CFG.
  - `ters`: A string representing the terminals of the CFG.

- **Constructor:**
  - `CfgEpsUnitElim(String cfg)`: Constructs a CFG object with the provided string representation of the CFG. The constructor parses the input string and initializes the CFG instance.

- **Methods:**
  - `eliminateEpsilonRules()`: Eliminates epsilon rules from the CFG.
  - `eliminateUnitRules()`: Eliminates unit rules from the CFG

-- **Example:**
```java
    public static void main(String[] args) {
        // Example input representing a context-free grammar
        String grammarInput = "S;A;B;C#a;b;c;d;x#S/aAb,xB;A/Bc,C,c,d;B/CACA,e;C/A,b,e";
        CfgEpsUnitElim in = new CfgEpsUnitElim(grammarInput);
        
        // Eliminate epsilon productions and print the resulting grammar
        System.out.println(in.eliminateEpsilonRules());
        // Eliminate unit rules and print the resulting grammar
        System.out.println(in.eliminateUnitRules());
    }
```

## CfgLeftRecElim.java

### Context-Free Grammar (CFG) Left Recursion Elimination
Eliminating left recursion ensures that parsing algorithms can effectively process context-free grammars without encountering infinite loops.

The program eliminates left recursion from the CFG. The output modifies the input in place.

### Class Structure

The `CfgLeftRecElim` class consists of the following elements:

- **Instance Variables:**
  - `contextFreeGrammar`: A Hashtable storing the production rules of the CFG.
  - `originalVariables`: An array storing the original variables of the CFG.

- **Constructor:**
  - `CfgLeftRecElim(String cfg)`: Constructs a CFG object with the provided string representation of the CFG. The constructor parses the input string and initializes the CFG instance.

- **Methods:**
  - `eliminateImmediateLeftRec(String currentKey)`: Eliminates immediate left recursion for a given variable in the CFG. This is a helper method that will be used in the function
  - `eliminateLeftRecursion()`: Eliminates left recursion from the CFG.

- **Example:**
```java
public static void main(String[] args) {
        // Example input representing a context-free grammar
        String grammarInput = "S;T;L#a;b;c;d;i#S/ScTi,La,Ti,b;T/aSb,LabS,i;L/SdL,Si";

        CfgLeftRecElim leftRecElim = new CfgLeftRecElim(grammarInput);

        System.out.println("Grammar after left recursion elimination:");
        System.out.println(leftRecElim.eliminateLeftRecursion());
    }
```

## CfgFirstFollow.java
Computing the First and Follow sets of a CFG is crucial for guiding parsing algorithms and detecting syntax errors during the compilation process.
The program calculates the First and Follow sets for each variable in a Context Free Grammar (CFG). The output modifies the input in place.

### Class Structure

The `CfgFirstFollow` class consists of the following elements:

- **Instance Variables:**
  - `contextFreeGrammar`: A Hashtable storing the production rules of the CFG.
  - `First`: A Hashtable storing the calculated First sets of variables.
  - `Follow`: A Hashtable storing the calculated Follow sets of variables.
  - `originalVariables`: An array storing the original variables of the CFG.

- **Constructor:**
  - `CfgFirstFollow(String cfg)`: Constructs a CFG object with the provided string representation of the CFG. The constructor parses the input string and initializes the CFG instance.

- **Methods:**
  - `first()`: Calculates the First sets of each variable in the CFG.
  - `follow()`: Calculates the Follow sets of each variable in the CFG.
  - `stringify(Hashtable<String, ArrayList<String>> input)`: Converts a CFG Hashtable to a string representation.
  - `isVariable(char c)`: Checks if a given character is a variable in the CFG.

- **Example:**
```java
public static void main(String[] args) {
        // Example input representing a context-free grammar
        String grammarInput = "S;T;L#a;b;c;d;i#S/ScT,T;T/aSb,iaLb,e;L/SdL,S";

        CfgFirstFollow in = new CfgFirstFollow(grammarInput);

        System.out.println(in.first());
        System.out.println(in.follow());

    }
```
