package com.borland.jb.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class ByteToCharConverter {

	public static ByteToCharConverter getConverter(String encoding) {
		return new ByteToCharConverter(Charset.forName(encoding));
	}

	private CharsetDecoder decoder;

	private ByteToCharConverter(Charset charset) {
		this.decoder = charset.newDecoder();
	}

	public int getMaxCharsPerByte() {
		return 1;
	}

	public int convert(byte[] input, int inStart, int inEnd, char[] output, int outStart, int outEnd) throws CharacterCodingException {
		CharBuffer charBuffer = decoder.decode(ByteBuffer.wrap(input, inStart, inEnd));
		outEnd = Math.min(outEnd, charBuffer.remaining());
		charBuffer.get(output, outStart, outEnd);
		return inEnd;
	}

	void reset() {}

}
