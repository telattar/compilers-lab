# compilers-lab
Weekly compilers lab assignments, coded in java.

## RegExToNfa.java

Takes a regular expression as an input string in the following format:
```alphabet1;alphabet2#regularExpression```

Returns a string that represents the equivalent Non-deterministic finite automata, in the following format:
```acceptableState1;acceptableState2;andSoOn#alphabet1;alphabet2#transition1;transition2;andSoOn#initialState#acceptState```


## NfaToDfa.java

Takes a Non-deterministic finite automata as an input string in the following format:
```acceptableState1;acceptableState2;andSoOn#alphabet1;alphabet2#transition1;transition2;andSoOn#initialState#acceptState```

Returns a string that represents the equivalent Deterministic finite automata, in the same format.

## CfgEpsUnitElim.java

Takes a context-free grammar as an input string in the following format:
```variable1;variable2;andSoOn#terminal1;terminal2;andSoOn#rule1;rule2;andSoOn```

Eliminates all the epsilon-transition based rules, as well as the unit rules.

Returns a string that represents the updated CFG, in the same format.
