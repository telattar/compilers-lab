package csen1002.main.task2;

import java.util.*;
import java.util.Map.Entry;

/**
 * Write your info here
 * 
 * @name Tarteel Elattar
 * @id 49-2019
 * @labNumber 17
 */

//from the last task
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

class TransitionDFA {
	char transit;
	ArrayList<Integer> finalState;
	ArrayList<Integer> startState;

	public TransitionDFA(ArrayList<Integer> s, char t, ArrayList<Integer> f) {
		this.startState = s;
		this.transit = t;
		this.finalState = f;
	}
}

public class NfaToDfa {

	/**
	 * Constructs a DFA corresponding to an NFA
	 * 
	 * @param input A formatted string representation of the NFA for which an
	 *              equivalent DFA is to be constructed. The string representation
	 *              follows the one in the task description
	 */

	int startState;
	ArrayList<Integer> acceptState = new ArrayList<Integer>();
	ArrayList<Integer> starting = new ArrayList<Integer>();
	String[] alphabet;
	String[] splitted;
	ArrayList<TransitionDFA> dfaTransitions = new ArrayList<TransitionDFA>();
	ArrayList<ArrayList<Integer>> states = new ArrayList<ArrayList<Integer>>();

	public TreeMap<Integer, ArrayList<Integer>> epsilonClosure(ArrayList<Transition> t) {
		// returns a hash table of the epsilon closure of all the states
		TreeMap<Integer, ArrayList<Integer>> result = new TreeMap<Integer, ArrayList<Integer>>();

		Transition current;
		ArrayList<Integer> closureSet;
		for (int i = 0; i < t.size(); i++) {
			// originally, each state's epsilon closure is itself.
			closureSet = new ArrayList<Integer>();

			current = t.get(i);

			if (!result.containsKey(current.startState)) {
				closureSet.clear();
				closureSet.add(current.startState);
				if (current.transit == 'e') {
					if (!closureSet.contains(current.finalState))
						closureSet.add(current.finalState);
				}
				result.put(current.startState, closureSet);
			}

			for (int j = i + 1; j < t.size(); j++) {

				// if the state at j has start state = state at current and epsilon transit, add
				// it.
				if (t.get(j).startState == current.startState && t.get(j).transit == 'e') {
					ArrayList<Integer> modified = result.get(current.startState);

					if (!modified.contains(t.get(j).finalState))
						modified.add(t.get(j).finalState);

					result.put(current.startState, modified);
				}
			}

		}

		// insert the final state
		closureSet = new ArrayList<Integer>();
		closureSet.add(result.size());
		result.put(result.size(), closureSet);
		return result;

	}

	public TreeMap<Integer, ArrayList<Integer>> epsilonClosureMerged(TreeMap<Integer, ArrayList<Integer>> input) {
		// takes the input hash table then merges the states AND then sorts the
		// arrayList
		boolean change = false;

		while (!change) {
			for (Integer key : input.keySet()) {

				Integer currentKey = key;
				ArrayList<Integer> val = input.get(currentKey);
				ArrayList<Integer> valCopy = new ArrayList<Integer>();
				ArrayList<Integer> extra;

				for (Integer state : val) {
					if (state == currentKey)
						continue;
					if (input.get(state).size() == 1)
						continue;
					
					val = input.get(currentKey);
					valCopy = new ArrayList<Integer>();
					valCopy.addAll(val);

					extra = input.get(state);
					for (Integer extraState : extra) {
						if (!val.contains(extraState)) {
							valCopy.add(extraState);
							change = true;
						}
					}

					input.put(currentKey, valCopy);

				}

			}

			if (change)
				change = false;
			else
				break;
		}

		// SORT THE ARRAYsssss
		ArrayList<Integer> sorted;
		for (Integer key : input.keySet()) {
			sorted = input.get(key);
			Collections.sort(sorted);
			input.put(key, sorted);
		}

		return input;

	}

	public ArrayList<TransitionDFA> getTransitionsDFA(TreeMap<Integer, ArrayList<Integer>> epsilonClosure,
			ArrayList<Transition> transits, int start) {

		starting = epsilonClosure.get(start);

		// the variable states should contain all the states in the dfa
		// by the end of the function

		// to add the new states that we should find transitions for
		ArrayList<ArrayList<Integer>> newStates = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> finalStates = new ArrayList<Integer>();
		ArrayList<TransitionDFA> dfaTrans = new ArrayList<TransitionDFA>();
		TransitionDFA trans;
		boolean present;
		// initially, add the start state
		newStates.add(starting);

		// get all the possible transitions in the DFA
		while (!newStates.isEmpty()) {
			ArrayList<Integer> state = newStates.get(0);
			for (String a : alphabet) {
				char current = a.charAt(0);
				finalStates = new ArrayList<Integer>();

				for (Integer s : state) {

					for (Transition t : transits) {

						if (t.startState == s && t.transit == current) {
							// add all the states in the epsilon closure to finalState
							for (Integer x : epsilonClosure.get(t.finalState)) {
								if (!finalStates.contains(x))
									finalStates.add(x);
							}
						}
					}

				}

				// we should add the final state to new states
				Collections.sort(finalStates);

				// if final states is empty, add -1 to dfa states
				if (finalStates.isEmpty())
					finalStates.add(-1);

				trans = new TransitionDFA(state, current, finalStates);

				present = false;
				for (TransitionDFA d : dfaTrans) {
					if (d.startState.equals(state) && d.transit == current && d.finalState.equals(finalStates))
						present = true;
				}
				if (!present)
					dfaTrans.add(trans);

				if (!newStates.contains(finalStates) && !states.contains(finalStates))
					newStates.add(finalStates);

				// and also remove the current state and add it to dfa states
				// if not already there
				if (!states.contains(state))
					states.add(state);

				newStates.remove(state);
			}
		}

		return dfaTrans;

	}

	public ArrayList<ArrayList<Integer>> sortArray(ArrayList<ArrayList<Integer>> array) {
		ArrayList<Integer> tempi;
		int smaller;
		boolean has = true;

		for (int i = 0; i < array.size(); i++) {
			for (int j = i + 1; j < array.size(); j++) {

				if (array.get(i).size() > array.get(j).size()) {
					// in this case if one contains the other swap
					for (int q = 0; q < array.get(j).size(); q++) {
						if (array.get(i).get(q) != array.get(j).get(q))
							has = false;
					}
					if (has) {
						tempi = array.get(i);
						array.set(i, array.get(j));
						array.set(j, tempi);
					}
					has = true;

				}

				smaller = array.get(i).size() > array.get(j).size() ? array.get(j).size() : array.get(i).size();
				for (int k = 0; k < smaller; k++) {
					if (array.get(i).get(k) > array.get(j).get(k)) {
						// swap
						tempi = array.get(i);
						array.set(i, array.get(j));
						array.set(j, tempi);
						break;
					} else if (array.get(i).get(k) < array.get(j).get(k))
						break;

				}

			}
		}

		return array;
	}


	public void sortDFA() {
		// sort transitions
		TransitionDFA tempi;
		int smaller;
		boolean has = true;

		for (int i = 0; i < dfaTransitions.size(); i++) {
			for (int j = i + 1; j < dfaTransitions.size(); j++) {

				// if the start states are not similar
				if (!dfaTransitions.get(i).startState.equals(dfaTransitions.get(j).startState)) {

					if (dfaTransitions.get(i).startState.size() > dfaTransitions.get(j).startState.size()) {
						// in this case if one contains the other swap
						for (int q = 0; q < dfaTransitions.get(j).startState.size(); q++) {
							if (dfaTransitions.get(i).startState.get(q) != dfaTransitions.get(j).startState.get(q))
								has = false;
						}
						if (has) {
							tempi = dfaTransitions.get(i);
							dfaTransitions.set(i, dfaTransitions.get(j));
							dfaTransitions.set(j, tempi);
						}
						has = true;
					}

					smaller = dfaTransitions.get(i).startState.size() > dfaTransitions.get(j).startState.size()
							? dfaTransitions.get(j).startState.size()
							: dfaTransitions.get(i).startState.size();
					for (int k = 0; k < smaller; k++) {
						if (dfaTransitions.get(i).startState.get(k) > dfaTransitions.get(j).startState.get(k)) {
							// swap
							tempi = dfaTransitions.get(i);
							dfaTransitions.set(i, dfaTransitions.get(j));
							dfaTransitions.set(j, tempi);
							break;
						} else if (dfaTransitions.get(i).startState.get(k) < dfaTransitions.get(j).startState.get(k))
							break;
					}

				}

				// if the alphabets arent the same
				else if (dfaTransitions.get(i).transit != dfaTransitions.get(j).transit) {
					if (dfaTransitions.get(i).transit > dfaTransitions.get(j).transit) {
						// swap
						tempi = dfaTransitions.get(i);
						dfaTransitions.set(i, dfaTransitions.get(j));
						dfaTransitions.set(j, tempi);
					}
				}

				// else sort by the final state
				else if (!dfaTransitions.get(i).finalState.equals(dfaTransitions.get(j).finalState)) {
					if (dfaTransitions.get(i).finalState.size() > dfaTransitions.get(j).finalState.size()) {
						// in this case if one contains the other swap
						for (int q = 0; q < dfaTransitions.get(j).finalState.size(); q++) {
							if (dfaTransitions.get(i).finalState.get(q) != dfaTransitions.get(j).finalState.get(q))
								has = false;
						}
						if (has) {
							tempi = dfaTransitions.get(i);
							dfaTransitions.set(i, dfaTransitions.get(j));
							dfaTransitions.set(j, tempi);
						}
						has = true;
					}

					smaller = dfaTransitions.get(i).finalState.size() > dfaTransitions.get(j).finalState.size()
							? dfaTransitions.get(j).finalState.size()
							: dfaTransitions.get(i).finalState.size();
					for (int k = 0; k < smaller; k++) {
						if (dfaTransitions.get(i).finalState.get(k) > dfaTransitions.get(j).finalState.get(k)) {
							// swap
							tempi = dfaTransitions.get(i);
							dfaTransitions.set(i, dfaTransitions.get(j));
							dfaTransitions.set(j, tempi);
							break;
						} else if (dfaTransitions.get(i).finalState.get(k) < dfaTransitions.get(j).finalState.get(k))
							break;

					}

				}

			}
		}
	}

	public NfaToDfa(String input) {
		// get all the transitions from the input
		splitted = input.split("#");

		String[] transits = splitted[2].split(";");
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		for (String s : transits)
			transitions.add(new Transition(Integer.parseInt(s.split(",")[0]), s.split(",")[1].charAt(0),
					Integer.parseInt(s.split(",")[2])));

		// alphabet
		alphabet = splitted[1].split(";");

		startState = Integer.parseInt(splitted[3]);
		String[] acceptSt = splitted[4].split(";");

		for (String s : acceptSt)
			acceptState.add(Integer.parseInt(s));

		// epsilon closure
		TreeMap<Integer, ArrayList<Integer>> epsilonC = epsilonClosure(transitions);

		// merged epsilon closure
		epsilonC = epsilonClosureMerged(epsilonC);

		// get the DFA transitions
		dfaTransitions = getTransitionsDFA(epsilonC, transitions, startState);

		// get the states from the dfa
		for (TransitionDFA d : dfaTransitions) {
			if (!states.contains(d.startState))
				states.add(d.startState);
			if (!states.contains(d.finalState))
				states.add(d.finalState);
		}

		// sort DFA transitions

		sortDFA();
		int smaller = 0;

	}

	/**
	 * @return Returns a formatted string representation of the DFA. The string
	 *         representation follows the one in the task description
	 */
	@Override
	public String toString() {
		String output = "";
		String s = "";

		states = sortArray(states);

		for (ArrayList<Integer> in : states) {
			for (Integer x : in) {
				s += x;
				s += "/";
			}
			output += s.substring(0, s.length() - 1) + ";";
			s = "";
		}

		output = output.substring(0, output.length() - 1);
		output += "#" + splitted[1] + "#";

		String str;
		for (TransitionDFA dfa : dfaTransitions) {
			str = "";
			for (Integer st : dfa.startState)
				str += st + "/";

			output += str.substring(0, str.length() - 1) + "," + dfa.transit + ",";

			str = "";
			for (Integer en : dfa.finalState)
				str += en + "/";

			output += str.substring(0, str.length() - 1) + ";";

		}
		output = output.substring(0, output.length() - 1) + "#";

		// get the start states
		ArrayList<ArrayList<Integer>> allStartStates = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> in : states) {

			// it has to contain the whole start state
			if (!starting.containsAll(in))
				continue;

			allStartStates.add(in);
		}

		// sort the array allStartStates
		allStartStates = sortArray(allStartStates);

		// now print the start states :)

		for (ArrayList<Integer> in : allStartStates) {
			for (Integer x : in) {
				s += x;
				s += "/";
			}
			output += s.substring(0, s.length() - 1) + ";";
			s = "";
		}

		output = output.substring(0, output.length() - 1) + "#";

		// accept states

		ArrayList<Integer> accepted = new ArrayList<Integer>();
		for (ArrayList<Integer> in : states) {

			accepted = new ArrayList<Integer>(acceptState);
			accepted.retainAll(in);

			if (accepted.size() > 0) {
				for (Integer x : in) {
					s += x;
					s += "/";
				}

				output += s.substring(0, s.length() - 1) + ";";

				s = "";
			}

		}

		output = output.substring(0, output.length() - 1);

		return output;
	}

//	public static void main(String[] args) {
//		NfaToDfa nfaToDfa= new NfaToDfa("0;1;2;3;4;5;6;7;8;9#i;s;y#0,i,1;1,e,5;2,e,3;3,e,5;4,e,0;4,e,2;5,y,6;6,s,7;7,e,4;7,e,9;8,e,4;8,e,9#8#9");
//		
//	}
	
}
