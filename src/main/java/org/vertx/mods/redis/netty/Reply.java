package org.vertx.mods.redis.netty;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;

public interface Reply<T> {
  byte[] CRLF = new byte[] { RedisDecoder.CR, RedisDecoder.LF };

  T data();
  void write(ChannelBuffer os) throws IOException;
}
