package csen1002.main.task5;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Write your info here
 * 
 * @name Tarteel Elattar
 * @id 49-2019
 * @labNumber 17
 */

public class CfgLeftRecElim {
	Hashtable<String, ArrayList<String>> contextFreeGrammar = new Hashtable<String, ArrayList<String>>();
	String[] originalVariables;
	String[] terminalArray;
	String variables = "";
	String terminals = "";

	ArrayList<String> replacement = new ArrayList<String>();
	ArrayList<String> terminal = new ArrayList<String>();

	/**
	 * Constructs a Context Free Grammar
	 * 
	 * @param cfg A formatted string representation of the CFG. The string
	 *            representation follows the one in the task description
	 */
	public CfgLeftRecElim(String cfg) {
		// start parsing the input string
		String[] splitted = cfg.split("#");
		variables = splitted[0];
		terminals = splitted[1];

		String[] grammar = splitted[2].split(";");
		originalVariables = variables.split(";");
		String variable;
		ArrayList<String> terminal;

		String[] var;
		for (String g : grammar) {
			var = g.split("/");
			variable = var[0];
			terminalArray = g.split("/")[1].split(",");
			terminal = new ArrayList<String>();

			for (String te : terminalArray)
				terminal.add(te);

			contextFreeGrammar.put(variable, terminal);

		}
	}

	/**
	 * @return Returns a formatted string representation of the CFG. The string
	 *         representation follows the one in the task description
	 */
	@Override
	public String toString() {
		for (String var : originalVariables) {
			if (contextFreeGrammar.keySet().contains(var + "'"))
				variables += ";" + var + "'";
		}
		StringBuilder result = new StringBuilder(variables + "#" + terminals + "#");

		for (String v : variables.split(";")) {
			result.append(v + "/");

			for (String terminal : contextFreeGrammar.get(v))
				result.append(terminal + ",");

			result.deleteCharAt(result.length() - 1);
			result.append(";");

		}

		result.deleteCharAt(result.length() - 1);

		return result.toString();
	}

	/**
	 * Eliminates Left Recursion from the grammar
	 */

	public void eliminateImmediateLeftRec(String currentKey) {
		Hashtable<String, ArrayList<String>> additionalRules = new Hashtable<String, ArrayList<String>>();
		String newVariable = "";
		boolean isLeftRec;
		replacement = new ArrayList<String>();
		terminal = new ArrayList<String>();

		isLeftRec = false;
		terminal = contextFreeGrammar.get(currentKey);

		// check the rules. does any of them start with the variable itself?
		for (String t : terminal) {
			if (!(t.charAt(0) + "").equals(currentKey))
				continue;

			isLeftRec = true;

			newVariable = currentKey + "'";
			replacement.add(t.substring(1) + newVariable);

		}

		if (isLeftRec) {

			// add or epsilon to the introduced rule
			replacement.add("e");
			// remove the immediate left recursion from the original rule
			terminal.removeIf(s -> s.charAt(0) == currentKey.charAt(0));

			// add the new variable to the end of each terminal
			for (int i = 0; i < terminal.size(); i++)
				terminal.set(i, terminal.get(i) + newVariable);

			// System.out.println(terminal);
			additionalRules.put(newVariable, new ArrayList<String>(replacement));
		}

		// to avoid concurrent modification errors, pour all the additional rules to the
		// original hash table
		contextFreeGrammar.putAll(additionalRules);
	}

	public void eliminateLeftRecursion() {
		Hashtable<String, ArrayList<String>> replace = new Hashtable<String, ArrayList<String>>();
		replacement = new ArrayList<String>();
		terminal = new ArrayList<String>();
		String modified = "";
		int insertIndex;
		boolean change = false;

		//eliminate immediate left recursion on the start variable
		eliminateImmediateLeftRec("S");

		// loop on the grammar hash table
		while (!change) {
			// loop on all the original variables and make sure there is no general left
			// recursion
			// this is done by making sure no rule starts with a variable that was defined
			// before it..
			for (int i = 1; i < originalVariables.length; i++) {
				terminal = contextFreeGrammar.get(originalVariables[i]);
				for (int j = 0; j < i; j++) {

					for (String t : terminal) {
						if (!(t.charAt(0) + "").equals(originalVariables[j]))
							continue;

						replacement.clear();
						for (String s : contextFreeGrammar.get(originalVariables[j])) {
							modified = t.replaceFirst(originalVariables[j], s);
							replacement.add(modified);
						}

						replace.put(t, new ArrayList<String>(replacement));
					}
					// insert in the positions specified in the lecture
					for (String s : replace.keySet()) {
						insertIndex = terminal.indexOf(s);
						terminal.remove(s);
						terminal.addAll(insertIndex, replace.get(s));
						change = true;

					}
					replace.clear();

				}
				eliminateImmediateLeftRec(originalVariables[i]);

			}

			if (change)
				change = false;
			else
				break;
		}

	}

}
