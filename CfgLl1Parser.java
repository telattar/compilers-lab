package csen1002.main.task8;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Stack;
import java.util.StringJoiner;

/**
 * Write your info here
 * 
 * @name Tarteel Elattar
 * @id 49-2019
 * @labNumber 17
 */

class Grammar {
	String rule;
	String associatedFirst, associatedFollow;

	public Grammar(String r, String aFirst, String aFollow) {
		this.rule = r;
		this.associatedFirst = aFirst;
		this.associatedFollow = aFollow;
	}
}

class parsingTableEntry {
	String variable;
	String terminal;
	String rule;

	public parsingTableEntry(String variable, String terminal, String rule) {
		this.variable = variable;
		this.terminal = terminal;
		this.rule = rule;
	}

}

public class CfgLl1Parser {

	String[] originalVariables, originalTerminals;
	Hashtable<String, ArrayList<Grammar>> helper = new Hashtable<String, ArrayList<Grammar>>();
	HashSet<parsingTableEntry> parsingTable = new HashSet<parsingTableEntry>();

	/**
	 * Constructs a Context Free Grammar
	 * 
	 * @param cfg A formatted string representation of the CFG, the First sets of
	 *            each right-hand side, and the Follow sets of each variable. The
	 *            string representation follows the one in the task description
	 */
	public CfgLl1Parser(String input) {
		String[] splitted = input.split("#");
		originalVariables = splitted[0].split(";");
		originalTerminals = splitted[1].split(";");

		// the input contains the original context free grammar
		String[] CFG = splitted[2].split(";");

		// the input contains the first of each rule linked a variable
		String[] allFirst = splitted[3].split(";");

		// it also contains the variables' follow
		String[] allFollow = splitted[4].split(";");

		// construct the helper table for the parsing table
		String follow = "";
		String[] first, rules;
		ArrayList<Grammar> association = new ArrayList<Grammar>();
		for (int i = 0; i < originalVariables.length; i++) {
			first = allFirst[i].split("/")[1].split(",");
			rules = CFG[i].split("/")[1].split(",");
			follow = allFollow[i].split("/")[1].split(",")[0];

			// each element in the array corresponds to the first of each rule,
			// consecutively.
			for (int j = 0; j < first.length; j++)
				association.add(new Grammar(rules[j], first[j], follow));

			helper.put(originalVariables[i], new ArrayList<Grammar>(association));
			association.clear();

		}

		// construct parsing table
		// for each variable, we will see which rule can result in a terminal being
		// generated from it
		// this terminal also should be at the start of the generation, so we will
		// consider the first of the variable.
		// if the first contains an epsilon e, then the rule can generate any of the
		// terminals in the variables's follow.

		for (String variable : helper.keySet()) {
			for (Grammar g : helper.get(variable)) {

				// each char in the first string is a terminal
				for (char f : g.associatedFirst.toCharArray()) {
					if (f == 'e') {
						for (char flw : g.associatedFollow.toCharArray())
							parsingTable.add(new parsingTableEntry(variable, flw + "", g.rule));

					} else
						parsingTable.add(new parsingTableEntry(variable, f + "", g.rule));
				}

			}
		}
	}

	// returns the parsing table rule given the variable and the terminal
	public String getRule(String var, String terminal) {
		for (parsingTableEntry entry : parsingTable) {
			if (entry.variable.equals(var) && entry.terminal.equals(terminal))
				return entry.rule;
		}
		return null;
	}

	/**
	 * @param input The string to be parsed by the LL(1) CFG.
	 * 
	 * @return A string encoding a left-most derivation.
	 */
	public String parse(String input) {
		// let's use the predictive parsing table
		// S is our start variable, we always start by referring to it when generating
		// the left-most derivation.
		// the dollar sign $ denotes the end of the stack/input. when the last element
		// in both the input and the stack is $, we know that we are done.
		// if the input string is done but the $ is not reached, or vice versa for the
		// stack, stop the process and return an ERROR at the end of the derivation
		// generated so far.

		ArrayList<String> result = new ArrayList<String>();
		input += "$";
		Stack<String> variableStack = new Stack<String>();
		variableStack.push("$");
		variableStack.push("S");
		result.add("S");
		String currentTop = "";
		String modified = "";
		String currentRule = "";
		int i = 0;
		while (i < input.length()) {
			currentTop = variableStack.pop();

			// if it's a variable, match a rule
			if (Character.isUpperCase(currentTop.charAt(0))) {
				currentRule = getRule(currentTop, input.charAt(i) + "");
				// if no rule is found for this variable and the terminal (the current char in
				// the input) return error
				if (currentRule == null) {
					result.add("ERROR");
					break;
				}
				// if there is a rule, we will push it into the stack
				for (char c : new StringBuilder(currentRule).reverse().toString().toCharArray()) {
					// we do not push epsilon rules.
					if (c != 'e')
						variableStack.push(c + "");
				}

				// and it will be included as a derivation step
				// this is done by removing the first occurrence of the currentTop and adding
				// the rule instead
				// if the rule is epsilon, we just remove the occurrence.
				if (currentRule.equals("e"))
					modified = result.get(result.size() - 1).replaceFirst(currentTop, "");
				else
					modified = result.get(result.size() - 1).replaceFirst(currentTop, currentRule);

				result.add(modified);
			}

			// if it's a terminal, and it is not equal to the character i'm pointing at,
			// then return error
			else {
				if (input.charAt(i) != currentTop.charAt(0)) {
					result.add("ERROR");
					break;
				} else
					i++;
			}
		}

		StringJoiner output = new StringJoiner(";");
		for (String s : result) {
			output.add(s);
		}
		return output.toString();
	}

}
