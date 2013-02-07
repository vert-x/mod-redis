package com.jetdrone.vertx.mods.redis.util;

/**
* A hashmap friendly key.
* <p/>
* User: sam
* Date: 7/28/11
* Time: 7:03 PM
*/
public class BytesKey extends BytesValue implements Comparable<BytesKey> {
  private final int hashCode;

  @Override
  public boolean equals(Object o) {
    BytesKey other = (BytesKey) o;
    return o instanceof BytesKey && hashCode == other.hashCode && equals(bytes, other.bytes);
  }

  public BytesKey(byte[] bytes) {
    super(bytes);
    int hashCode = 0;
    for (byte aByte : this.bytes) {
      hashCode += 43 * aByte;
    }
    this.hashCode = hashCode;
  }

  public int hashCode() {
    return hashCode;
  }

    // TODO: hacked in
    private int compare(byte[] left, byte[] right) {
        for (int i = 0, j = 0; i < left.length && j < right.length; i++, j++) {
            int a = (left[i] & 0xff);
            int b = (right[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return left.length - right.length;
    }

  @Override
  public int compareTo(BytesKey o) {
    return compare(this.bytes, o.bytes);
  }
}
