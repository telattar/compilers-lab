package csen1002.main.task6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

/**
 * Write your info here
 * 
 * @name Tarteel Elatar
 * @id 49-2019
 * @labNumber 17
 */

public class CfgFirstFollow {
	Hashtable<String, ArrayList<String>> contextFreeGrammar = new Hashtable<String, ArrayList<String>>();
	Hashtable<String, ArrayList<String>> First = new Hashtable<String, ArrayList<String>>();
	Hashtable<String, ArrayList<String>> Follow = new Hashtable<String, ArrayList<String>>();
	String[] originalVariables;

	/**
	 * Constructs a Context Free Grammar
	 * 
	 * @param cfg A formatted string representation of the CFG. The string
	 *            representation follows the one in the task description
	 */
	public CfgFirstFollow(String cfg) {
		// start parsing the input string
		String[] splitted = cfg.split("#");
		String variables = splitted[0];

		String[] grammar = splitted[2].split(";");
		originalVariables = variables.split(";");
		String variable;
		ArrayList<String> terminal;
		String[] terminalArray;

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

	public boolean isVariable(char c) {
		return Arrays.asList(originalVariables).contains(c + "");
	}

	/**
	 * Calculates the First Set of each variable in the CFG.
	 * 
	 * @return A string representation of the First of each variable in the CFG,
	 *         formatted as specified in the task description.
	 */

	public String stringify(Hashtable<String, ArrayList<String>> input) {
		StringBuilder result = new StringBuilder();
		for (String v : originalVariables) {
			result.append(v + "/");
			Collections.sort(input.get(v));
			for (String t : input.get(v))
				result.append(t);

			result.append(";");

		}
		return result.deleteCharAt(result.length() - 1).toString();

	}

	public String first() {
		ArrayList<String> terminal = new ArrayList<String>();
		ArrayList<String> firstArray = new ArrayList<String>();

		// generate an array of string for each variable
		for (String var : originalVariables)
			First.put(var, new ArrayList<String>());

		boolean change = true;

		while (change) {
			change = false;

			for (String key : originalVariables) {

				terminal = contextFreeGrammar.get(key);
				firstArray = First.get(key);

				for (String ter : terminal) {

					// if it starts with a terminal char, add it and move on to the next one
					if (!isVariable(ter.charAt(0))) {
						if (!firstArray.contains(ter.charAt(0) + "")) {
							firstArray.add(ter.charAt(0) + "");
							change = true;
						}
						continue;

					}

					// otherwise, loop as needed
					for (int i = 0; i < ter.length(); i++) {
						if (!isVariable(ter.charAt(i))) {
							if (!firstArray.contains(ter.charAt(i) + "")) {
								firstArray.add(ter.charAt(i) + "");
								change = true;
							}
							break;
						}
						// if it is a variable
						// take the variable's first elements except for the epsilon
						// unless this is the last element in the terminal.
						// then continue to the next iteration
						if (isVariable(ter.charAt(i))) {
							if (key.equals(ter.charAt(i) + "")) {
								if (First.get(key).contains("e")) {
									if (i == ter.length() - 1 && !firstArray.contains("e")) {
										firstArray.add("e");
										change = true;
										break;
									} else
										continue;
								} else
									break;
							}

							if (First.get(ter.charAt(i) + "").isEmpty())
								break;

							// if it has no epsilon, just put it all and move on.
							if (!First.get(ter.charAt(i) + "").contains("e")) {
								for (String s : First.get(ter.charAt(i) + "")) {
									if (!firstArray.contains(s)) {
										firstArray.add(s);
										change = true;
									}
								}
								break;
							}

							for (String s : First.get(ter.charAt(i) + "")) {
								if (!firstArray.contains(s)) {
									// if it is not an epsilon rule
									if (!s.equals("e")) {
										firstArray.add(s);
										change = true;
									}
									// if it IS an epsilon rule
									else {
										// is it the last index of the terminal?
										if (i == ter.length() - 1) {
											firstArray.add(s);
											change = true;
										} else
											continue;

									}
								}
							}

						}
					}

				}
			}

		}

		StringBuilder result = new StringBuilder();
		for (String v : originalVariables) {
			result.append(v + "/");
			Collections.sort(First.get(v));
			for (String t : First.get(v))
				result.append(t);

			result.append(";");

		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	/**
	 * Calculates the Follow Set of each variable in the CFG.
	 * 
	 * @return A string representation of the Follow of each variable in the CFG,
	 *         formatted as specified in the task description.
	 */
	public String follow() {
		// get the first of each variable
		first();

		ArrayList<String> currentTerminal = new ArrayList<String>();
		ArrayList<String> followArray = new ArrayList<String>();
		ArrayList<String> toAdd = new ArrayList<String>();

		// generate an array of string for each variable
		for (String var : originalVariables)
			Follow.put(var, new ArrayList<String>());

		// the start variable always has a dollar $ sign
		followArray.add("$");
		Follow.put("S", followArray);
		boolean change = true;
		boolean hasEpsilon = false;
		String currentVar = "";

		while (change) {
			change = false;

			for (String key : originalVariables) {
				currentTerminal = contextFreeGrammar.get(key);

				for (String rule : currentTerminal) {

					for (int i = 0; i < rule.length(); i++) {
						if (!isVariable(rule.charAt(i)))
							continue;

						currentVar = rule.charAt(i) + "";
						followArray = Follow.get(currentVar);

						// if it is a variable AND the last element of the rule,
						// then the steps that apply to hasEpsilon also apply to it
						if (i == rule.length() - 1)
							hasEpsilon = true;

						for (int j = i + 1; j < rule.length(); j++) {
							if (!isVariable(rule.charAt(j))) {
								hasEpsilon = false;
								if (!followArray.contains(rule.charAt(j) + "") && !(rule.charAt(j) + "").equals("e")) {
									followArray.add(rule.charAt(j) + "");
									change = true;
								}
								break;

							}

							else if (isVariable(rule.charAt(j)) && !currentVar.equals(rule.charAt(j) + "")) {
								toAdd = First.get(rule.charAt(j) + "");

								if (toAdd.contains("e"))
									hasEpsilon = true;
								else
									hasEpsilon = false;

								for (String s : toAdd) {
									if (!followArray.contains(s) && !s.equals("e")) {
										followArray.add(s);
										change = true;
									}
								}

								if (!hasEpsilon)
									break;

							}

						}

						if (hasEpsilon) {
							hasEpsilon = false;

							if (!key.equals(currentVar)) {
								toAdd = Follow.get(key);
								for (String s : toAdd) {
									if (!followArray.contains(s)) {
										followArray.add(s);
										change = true;
									}
								}
							}
						}

					}
				}
			}

		}

		return stringify(Follow);
	}

}
