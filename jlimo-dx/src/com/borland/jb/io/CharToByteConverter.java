package com.borland.jb.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

class CharToByteConverter {

	public static CharToByteConverter getConverter(String encoding) {
		return new CharToByteConverter(Charset.forName(encoding));
	}

	private CharsetEncoder encoder;

	private CharToByteConverter(Charset charset) {
		this.encoder = charset.newEncoder();
	}

	public void setSubstitutionMode(boolean mode) {
	}

	public int getMaxBytesPerChar() {
		return 1;
	}

	public int convert(char[] input, int inStart, int inEnd, byte[] output, int outStart, int outEnd) throws UnknownCharacterException, CharacterCodingException {
		ByteBuffer byteBuffer = encoder.encode(CharBuffer.wrap(input, inStart, inEnd));
		outEnd = Math.min(outEnd, byteBuffer.remaining());
		byteBuffer.get(output, outStart, outEnd);
		return inEnd;
	}

}
