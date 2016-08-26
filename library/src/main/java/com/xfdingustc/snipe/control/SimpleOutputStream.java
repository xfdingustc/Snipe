package com.xfdingustc.snipe.control;

import java.io.IOException;
import java.io.OutputStream;

public class SimpleOutputStream extends OutputStream {

	protected byte[] buf;
	protected int count;

	public SimpleOutputStream(int size) {
		if (size >= 0) {
			buf = new byte[size];
		} else {
			throw new IllegalArgumentException("size < 0");
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

	private void expand(int i) {
		/* Can the buffer handle @i more bytes, if not expand it */
		if (count + i <= buf.length) {
			return;
		}

		byte[] newbuf = new byte[(count + i) * 2];
		System.arraycopy(buf, 0, newbuf, 0, count);
		buf = newbuf;
	}

	// API
	public void reset() {
		count = 0;
	}

	// API
	public byte[] getBuffer() {
		return buf;
	}

	// API
	public int getSize() {
		return count;
	}

	// API
	public void writeZero(int bytes) {
		if (bytes < 0) {
			throw new IllegalArgumentException("size < 0");
		}
		expand(bytes);
		int index = count;
		for (int i = 0; i < bytes; i++, index++) {
			buf[index] = 0;
		}
		count += bytes;
	}

	// TODO - check
	public void clear(int pos, int bytes) {
		for (int i = 0; i < bytes; i++, pos++) {
			buf[pos] = 0;
		}
	}

	// TODO - check
	// API
	public void writei32(int pos, int value) {
		buf[pos] = (byte)(value);
		buf[pos + 1] = (byte)(value >> 8);
		buf[pos + 2] = (byte)(value >> 16);
		buf[pos + 3] = (byte)(value >> 24);
	}

	// API - for debug
	public String toString(int offset) {
		return new String(buf, offset, count - offset);
	}

	@Override
	public String toString() {
		return new String(buf, 0, count);
	}

	private static void checkOffsetAndCount(int arrayLength, int offset, int count) {
		if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
			throw new ArrayIndexOutOfBoundsException(count);
		}
	}

	@Override
	public void write(byte[] buffer, int offset, int len) {
		checkOffsetAndCount(buffer.length, offset, len);
		if (len == 0) {
			return;
		}
		expand(len);
		System.arraycopy(buffer, offset, buf, this.count, len);
		this.count += len;
	}

	@Override
	public void write(int oneByte) {
		if (count == buf.length) {
			expand(1);
		}
		buf[count++] = (byte)oneByte;
	}

}
