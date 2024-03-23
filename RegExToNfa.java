package csen1002.main.task1;

import java.util.*;

/**
 * Write your info here
 * 
 * @name Tarteel Elattar
 * @id 49-2019
 * @labNumber 17
 */
class Transition {
	int startState;
	char transit;
	int finalState;

	public Transition(int s, char t, int f) {
		this.startState = s;
		this.transit = t;
		this.finalState = f;

	}
}

//an operator is a set of transitions, has general start and final states. assume it is an NFA on its own
class Operator {
	int starting;
	int accepted;
	ArrayList<Transition> transitions;

	public Operator(int s, int f) {
		this.starting = s;
		this.transitions = new ArrayList<Transition>();
		this.accepted = f;
	}
}

public class RegExToNfa {

	/**
	 * Constructs an NFA corresponding to a regular expression based on Thompson's
	 * construction
	 * 
	 * @param input The alphabet and the regular expression in postfix notation for
	 *              which the NFA is to be constructed
	 */

	// checks if its input is an alphabet
	public boolean isAlphabet(String alpha, char input) {
		alpha += "e";
		for (int i = 0; i < alpha.length(); i++) {
			if (alpha.charAt(i) == input)
				return true;
		}
		return false;
	}

	String alphabet;
	Operator finalResult;
	ArrayList<Integer> acceptedStates = new ArrayList<Integer>();

	public RegExToNfa(String input) {
		// stack of current operators
		Stack<Operator> s = new Stack<Operator>();
		


		// a counter for each number of state
		int counter = 0;
		int start = 0;
		int accept = 0;

		String[] splitted = input.split("#");
		String regex = splitted[1];
		alphabet = splitted[0];

		for (int i = 0; i < regex.length(); i++) {

			Transition t;
			Operator n;

			// if it's an alphabet, create an NFA for it.
			if (isAlphabet(alphabet, regex.charAt(i))) {
				t = new Transition(counter++, regex.charAt(i), counter);
				n = new Operator(counter - 1, counter);
				n.transitions.add(t);
				counter++;
				s.push(n);
			}

			else if (regex.charAt(i) == '|') {
				// union operation

				// pop the latest two operators out of the current stack
				Operator operator2 = s.pop();
				Operator operator1 = s.pop();

				// i will modify operator2 then push it at the end
				operator2.transitions.addAll(operator1.transitions);

				// a new start state that transits into the whole thingy
				t = new Transition(counter, 'e', operator1.starting);
				operator2.transitions.add(t);

				t = new Transition(counter, 'e', operator2.starting);
				operator2.transitions.add(t);

				start = counter;

				counter++;

				// a new end state that the states go into
				t = new Transition(operator1.accepted, 'e', counter);
				operator2.transitions.add(t);

				t = new Transition(operator2.accepted, 'e', counter);
				operator2.transitions.add(t);

				accept = counter;

				operator2.starting = start;
				operator2.accepted = accept;

				s.push(operator2);
				counter++;
			}

			else if (regex.charAt(i) == '*') {
				// kleene closure -- pop the latest operator
				Operator current = s.pop();

				// add a transition from the final state to the initial state
				current.transitions.add(new Transition(current.accepted, 'e', current.starting));

				// a new state transit into the current initial state
				current.transitions.add(new Transition(counter, 'e', current.starting));
				current.starting = counter;
				counter++;

				// the end state transit into a new state
				current.transitions.add(new Transition(current.accepted, 'e', counter));
				current.accepted = counter;

				// the new start and end states should transit to each other
				current.transitions.add(new Transition(current.starting, 'e', current.accepted));

				counter++;

				s.push(current);
			}

			else if (regex.charAt(i) == '.') {
				// concatenation
				// pop the latest two operators out of the current stack
				Operator operator2 = s.pop();
				Operator operator1 = s.pop();

				// remove operator 1's start state
				// any transitions going out of operator 1's start state should go out of
				// operator 2's end state
				for (Transition tr : operator2.transitions) {
					if (tr.startState == operator2.starting)
						tr.startState = operator1.accepted;
				}

				operator2.transitions.addAll(operator1.transitions);
				operator2.starting = operator1.starting;
				s.push(operator2);
			}
		}


		finalResult = s.pop();
		
		if (!s.isEmpty())
			System.out.println("The stack should be empty at this point...");

	}

	/**
	 * @return 
	 * @return Returns a formatted string representation of the NFA. The string
	 *         representation follows the one in the task description
	 */
	@Override
	public String toString() {
		// integer array of the accepted states. should be sorted.
		String result = getAcceptedStates(finalResult);
		result += "#" + alphabet + "#";
		
		String transitions = getSortedTransitions(finalResult);
		result += transitions + "#" + finalResult.starting + "#" + finalResult.accepted;

		return result;
	}
	
	public String getAcceptedStates(Operator a) {
		//returns a string of the sorted, accepted states
		ArrayList<Integer> states = new ArrayList<Integer>();
		
		//insert all the accepted states
		for (Transition t : a.transitions) {
			if (!states.contains(t.startState))
				states.add(t.startState);
			if (!states.contains(t.finalState))
				states.add(t.finalState);
		}
		
		//bubble sort like in cs3
		int temp;
		for (int i=0; i<states.size(); i++) {
			for (int j=i+1; j<states.size(); j++) {
				temp = 0;
				if (states.get(i) > states.get(j)) {
					//swap
					temp = states.get(i);
					states.set(i, states.get(j));
					states.set(j, temp);
					
				}
			}
		}

		String result = "0;";
		for (int i=1; i<states.size()-1; i++)
			result+= states.get(i) + ";";
		
		return result+states.get(states.size()-1);

	}

	public String getSortedTransitions(Operator a) {
		//returns the transitions, sorted as required, in a string
		ArrayList<Transition> tr = a.transitions;
		Transition temp;
		//bubble sort
		for (int i=0; i<tr.size(); i++) {
			
			for (int j=i+1; j<tr.size(); j++) {
				
				temp = null;
				//sort by the start state
				if (tr.get(i).startState > tr.get(j).startState) {
					//swap
					temp = tr.get(i);
					tr.set(i, tr.get(j));
					tr.set(j, temp);
				}
				//if the start states are equal, sort by the alphabet
				else if (tr.get(i).startState == tr.get(j).startState) {
					if (tr.get(i).transit > tr.get(j).transit) {
						//swap
						temp = tr.get(i);
						tr.set(i, tr.get(j));
						tr.set(j, temp);
					}
					// if the transits are equal, sort by the destination
					else if (tr.get(i).transit == tr.get(j).transit) {
						if (tr.get(i).finalState > tr.get(j).finalState) {
						//swap
						temp = tr.get(i);
						tr.set(i, tr.get(j));
						tr.set(j, temp);
						}
					}
				}
				
			}
		}

		String result = tr.get(0).startState + "," + tr.get(0).transit + "," + tr.get(0).finalState+ ";";
		for (int i=1; i<tr.size()-1; i++)
			result+= tr.get(i).startState + "," + tr.get(i).transit + "," + tr.get(i).finalState+ ";";
		
		return result+tr.get(tr.size()-1).startState + "," + tr.get(tr.size()-1).transit + "," + tr.get(tr.size()-1).finalState;

			
		
	}


}
