// This file is part of PDQ (https://github.com/ProofDrivenQuerying/pdq) which is released under the MIT license.
// See accompanying LICENSE for copyright notice and full details.

package uk.ac.ox.cs.pdq.exceptions;




/**
 * Exception that occurrs when parameters cannot be loaded or set properly.
 *
 * @author Julien Leblay
 */
public class ParametersException extends RuntimeException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -315472460756924167L;

	/**
	 * Instantiates a new parameters exception.
	 *
	 * @param msg exception message.
	 */
	public ParametersException(String msg) {
		super(msg);
	}

	/**
	 * Instantiates a new parameters exception.
	 */
	public ParametersException() {
		super();
	}
}
