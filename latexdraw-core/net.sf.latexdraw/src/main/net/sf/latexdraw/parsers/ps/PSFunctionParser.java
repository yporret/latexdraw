/*
 * This file is part of LaTeXDraw
 * Copyright (c) 2005-2017 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
package net.sf.latexdraw.parsers.ps;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A postscript function parser.
 */
public class PSFunctionParser {
	/** The postscript function. */
	private final String function;
	private final List<PSArithemticCommand> commands;
	private final Map<String, Supplier<PSArithemticCommand>> factoryMap;

	/**
	 * Creates and parser from postscript functions.
	 * @param fct The function to parse.
	 * @throws InvalidFormatPSFunctionException If the function format is not valid.
	 * @since 3.0
	 */
	public PSFunctionParser(final String fct) throws InvalidFormatPSFunctionException {
		super();
		if(fct == null || fct.isEmpty()) throw new IllegalArgumentException();

		factoryMap = new HashMap<>();
		commands = new ArrayList<>();
		function = fct;

		factoryMap.put("add", () -> new PSAddCommand());
		factoryMap.put("mul", () -> new PSMulCommand());
		factoryMap.put("sub", () -> new PSSubCommand());
		factoryMap.put("sin", () -> new PSSinCommand());
		factoryMap.put("cos", () -> new PSCosCommand());
		factoryMap.put("div", () -> new PSDivCommand());
		factoryMap.put("idiv", () -> new PSIDivCommand());
		factoryMap.put("mod", () -> new PSModCommand());
		factoryMap.put("neg", () -> new PSNegCommand());
		factoryMap.put("exch", () -> new PSExchCommand());
		factoryMap.put("clear", () -> new PSClearCommand());
		factoryMap.put("dup", () -> new PSDupCommand());
		factoryMap.put("pop", () -> new PSPopCommand());
		factoryMap.put("roll", () -> null);
		factoryMap.put("sqrt", () -> null);
		factoryMap.put("exp", () -> new PSExpCommand());
		factoryMap.put("abs", () -> new PSAbsCommand());
		factoryMap.put("floor", () -> new PSFloorCommand());
		factoryMap.put("ceiling", () -> new PSCeilingCommand());
		factoryMap.put("count", () -> new PSCountCommand());
		factoryMap.put("x", () -> new PSPlotXVariable());
		factoryMap.put("log", () -> new PSLogCommand());

		parseFunction();
	}

	/**
	 * Checks whether the given equation is a valid post-fixed PS equation.
	 * @param eq The equation to check.
	 * @param min The X-min of the plotting.
	 * @param max The X-max of the plotting.
	 * @param nbPts The number of points to plot.
	 * @return True if the given equation is a valid post-fixed PS equation.
	 * @since 3.3
	 */
	public static boolean isValidPostFixEquation(final String eq, final double min, final double max, final double nbPts) {
		try {
			final PSFunctionParser fct = new PSFunctionParser(eq);
			final double gap = (max - min) / (nbPts - 1);

			for(double x = min; x < max; x += gap) {
				fct.getY(x);
			}

			return true;
		}catch(final NumberFormatException | ArithmeticException ex) {
			return false;
		}
	}


	/**
	 * @param x The X-coordinate used to compute the Y using the function.
	 * @return The y value corresponding to the given X value. Or Double.NaN is an arithmetic error occurs.
	 * @throws InvalidFormatPSFunctionException If the function is not correct.
	 * @throws ArithmeticException If an error occurs during the computation of the points (e.g. division by 0).
	 */
	public double getY(final double x) throws InvalidFormatPSFunctionException {
		final Deque<Double> stack = new ArrayDeque<>();

		commands.forEach(cmd -> cmd.execute(stack, x));

		if(stack.isEmpty()) throw new InvalidFormatPSFunctionException();

		return stack.pop();
	}

	/**
	 * Parses the function.
	 * @throws InvalidFormatPSFunctionException If the function is not correct.
	 * @throws NumberFormatException If the function is not correct.
	 */
	protected void parseFunction() throws NumberFormatException {
		int i = 0;
		final int lgth = function.length();
		final StringBuilder cmd = new StringBuilder();

		while(i < lgth) {
			cmd.delete(0, cmd.length());

			while(i < lgth && function.charAt(i) == ' ') {
				i++;
			}

			while(i < lgth && function.charAt(i) != ' ') {
				cmd.append(function.charAt(i++));
			}

			if(cmd.length() > 0) {
				commands.add(identifyCommand(cmd.toString()));
			}
		}
	}

	/**
	 * @param cmd The arithmetic command to analyse.
	 * @return The arithmetic instance corresponding to the given command.
	 * @throws InvalidFormatPSFunctionException If the function is not correct.
	 * @throws NumberFormatException If the function is not correct.
	 * @since 3.0
	 */
	protected PSArithemticCommand identifyCommand(final String cmd) throws NumberFormatException {
		if(cmd == null || cmd.isEmpty()) throw new InvalidFormatPSFunctionException();

		try {
			return factoryMap.getOrDefault(cmd, () -> new PSValue(Double.parseDouble(cmd))).get();
		}catch(final NumberFormatException ex) {
			throw new InvalidFormatPSFunctionException("Cannot parse: " + cmd); //$NON-NLS-1$
		}
	}
}
