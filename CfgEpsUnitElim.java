package csen1002.main.task4;

import java.util.*;
import java.util.Map.Entry;

/**
 * Write your info here
 * 
 * @name Tarteel Elattar
 * @id 49-2019
 * @labNumber 17
 */

class CFG {
	char variable;
	ArrayList<String> terminal;

	public CFG(char v) {
		this.variable = v;
		terminal = new ArrayList<String>();
	}
}

public class CfgEpsUnitElim {

	Hashtable<Character, ArrayList<String>> contextGrammar = new Hashtable<Character, ArrayList<String>>();
	String vars = "";
	String ters = "";

	/**
	 * Constructs a Context Free Grammar
	 * 
	 * @param cfg A formatted string representation of the CFG. The string
	 *            representation follows the one in the task description
	 */
	public CfgEpsUnitElim(String cfg) {

		// start parsing the input string
		String[] splitted = cfg.split("#");
		vars = splitted[0];
		ters = splitted[1];

		String[] grammar = splitted[2].split(";");

		String v;
		String[] t;
		CFG c;
		ArrayList<String> terminal;

		for (String g : grammar) {
			v = g.split("/")[0];
			t = g.split("/")[1].split(",");
			c = new CFG(v.charAt(0));
			terminal = new ArrayList<String>();

			for (String te : t)
				terminal.add(te);

			contextGrammar.put((Character) v.charAt(0), terminal);

		}

//		for (Character x : contextGrammar.keySet()) {
//			System.out.println(x);
//			Collections.sort(contextGrammar.get(x));
//			System.out.println(contextGrammar.get(x));
//		}
	}

	/**
	 * @return Returns a formatted string representation of the CFG. The string
	 *         representation follows the one in the task description
	 */
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(vars + "#" + ters + "#");

		for (String v : vars.split(";")) {
			Character key = v.charAt(0);
			Collections.sort(contextGrammar.get(key));
			result.append(v + "/");

			for (String terminal : contextGrammar.get(key))
				result.append(terminal + ",");

			result.deleteCharAt(result.length() - 1);
			result.append(";");

		}

		result.deleteCharAt(result.length() - 1);
		// System.out.println(result);

		return result.toString();
	}

	/**
	 * Eliminates Epsilon Rules from the grammar
	 */
	public void eliminateEpsilonRules() {

		ArrayList<String> update = new ArrayList<String>();
		StringBuilder substitute;
		ArrayList<Character> hadEps = new ArrayList<Character>();
		String up;
		boolean change = false;

		// loop on the whole grammar
		while (!change) {
			for (Character currentKey : contextGrammar.keySet()) {
				// if this variable doesn't have an epsilon transition,
				// continue to the next iteration.
				if (!contextGrammar.get(currentKey).contains("e"))
					continue;
				if (!hadEps.contains(currentKey))
					hadEps.add(currentKey);

				// loop on the cfg again.
				for (Character repeat : contextGrammar.keySet()) {

					for (String s : contextGrammar.get(repeat)) {
						if (s.contains(currentKey + "")) {
							update.add(s);
						}
					}

					while (!update.isEmpty()) {
						up = update.remove(0);
						// add it to the original set of terminals
						if (!contextGrammar.get(repeat).contains(up)) {
							// if this is an epsilon
							if (up.isEmpty()) {
								// is it an introduction to a new variable?
								if (repeat != currentKey) {
									if (!contextGrammar.get(repeat).contains("e") && !hadEps.contains(repeat)) {
										contextGrammar.get(repeat).add("e");
										change = true;
									}
								}
							} else if (!contextGrammar.get(repeat).contains(up))
								contextGrammar.get(repeat).add(up);
						}

						for (int i = 0; i < up.length(); i++) {
							if (up.charAt(i) != currentKey)
								continue;

							substitute = new StringBuilder(up);
							substitute.deleteCharAt(i);
							update.add(substitute.toString());
						}
					}
				}

				contextGrammar.get(currentKey).remove("e");
			}

			if (change)
				change = false;
			else
				break;
		}

	}

	/**
	 * Eliminates Unit Rules from the grammar
	 */
	public void eliminateUnitRules() {
		String[] allVariables = vars.split(";");
		ArrayList<String> sub = new ArrayList<String>();
		ArrayList<String> other;
		boolean change = false;

		while (!change) {
			for (Character currentKey : contextGrammar.keySet()) {
				// disregard the occurrence of the variable in the terminals (if any)
				contextGrammar.get(currentKey).remove(currentKey.toString());

				// if terminal contains one of the variables -> eliminate unit rule
				sub.clear();
				sub.addAll(contextGrammar.get(currentKey));
				for (String terminal : contextGrammar.get(currentKey)) {
					if (!Arrays.asList(allVariables).contains(terminal))
						continue;

					other = contextGrammar.get(terminal.charAt(0));
					for (String s : other) {
						if (!sub.contains(s))
							sub.add(s);
					}
					sub.remove(terminal);
					sub.removeAll(Arrays.asList(allVariables));
				}
				for (String s : sub) {
					if (!contextGrammar.get(currentKey).contains(s)) {
						contextGrammar.get(currentKey).add(s);
						change = true;
					}
				}

			}

			if (change)
				change = false;
			else
				break;
		}
		for (Character currentKey : contextGrammar.keySet())
			contextGrammar.get(currentKey).removeAll(Arrays.asList(allVariables));

	}


}
