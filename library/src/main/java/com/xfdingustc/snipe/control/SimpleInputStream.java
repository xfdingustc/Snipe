package com.xfdingustc.snipe.control;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class SimpleInputStream extends InputStream {
    private static final String TAG = SimpleInputStream.class.getSimpleName();
    protected byte[] buf;
    protected int count;
    protected int pos;
    protected int mark;

    public SimpleInputStream(int size) {
        if (size >= 0) {
            buf = new byte[size];
            count = size;
        } else {
            throw new IllegalArgumentException("size < 0");
        }
    }

    public SimpleInputStream(byte[] bytes) {
        buf = bytes;
        count = bytes.length;
    }

    // API
    public void expand(int maxSize) {
        if (maxSize > buf.length) {
            byte[] newbuf = new byte[maxSize];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
    }

    // API
    public void clear() {
        count = 0;
        pos = 0;
        mark = 0;
    }

    // API
    public byte[] getBuffer() {
        return buf;
    }

    // API
    public int getCount() {
        return count;
    }

    // API
    public String toString(int offset) {
        return new String(buf, offset, count - offset, Charset.forName("UTF-8"));
    }


    public void setRange(int offset, int bytes) {
        if (offset < 0 || bytes < 0 || offset + bytes > buf.length) {
            Log.d(TAG, "offset: " + offset + " bytes: " + bytes);
            throw new IllegalArgumentException("bad");
        }
        pos = offset;
        mark = offset;
        count = bytes;
    }

    // TODO - check
    // API
    public int readi32(int pos) {
        int result = (int) buf[pos] & 0xFF;
        result |= ((int) buf[pos + 1] & 0xFF) << 8;
        result |= ((int) buf[pos + 2] & 0xFF) << 8;
        result |= ((int) buf[pos + 3] & 0xFF) << 8;
        return result;
    }

    @Override
    public int available() {
        return count - pos;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void mark(int readlimit) {
        mark = pos;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() {
        return pos < count ? buf[pos++] & 0xFF : -1;
    }

    private static void checkOffsetAndCount(int arrayLength, int offset, int count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException(count);
        }
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) {
        checkOffsetAndCount(buffer.length, byteOffset, byteCount);

        // Are there any bytes available?
        if (this.pos >= this.count) {
            return -1;
        }
        if (byteCount == 0) {
            return 0;
        }

        int copylen = this.count - pos < byteCount ? this.count - pos : byteCount;
        System.arraycopy(this.buf, pos, buffer, byteOffset, copylen);
        pos += copylen;
        return copylen;
    }

    @Override
    public void reset() {
        pos = mark;
    }

    @Override
    public long skip(long byteCount) {
        if (byteCount <= 0) {
            return 0;
        }
        int temp = pos;
        pos = this.count - pos < byteCount ? this.count : (int) (pos + byteCount);
        return pos - temp;
    }
}
