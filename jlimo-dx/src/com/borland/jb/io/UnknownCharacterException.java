package com.borland.jb.io;

import java.io.CharConversionException;

class UnknownCharacterException extends CharConversionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8292160100835450662L;

	public UnknownCharacterException() {
		super();
	}

	public UnknownCharacterException(String s) {
		super(s);
	}

}
