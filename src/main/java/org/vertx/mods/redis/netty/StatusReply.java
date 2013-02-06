package org.vertx.mods.redis.netty;

import java.io.IOException;
import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;

public class StatusReply implements Reply<String> {
  public static final char MARKER = '+';
  private final String status;

  public StatusReply(String status) {
    this.status = status;
  }

  @Override
  public String data() {
    return status;
  }

  @Override
  public void write(ChannelBuffer os) throws IOException {
    os.writeByte(MARKER);
    os.writeBytes(status.getBytes(Charset.forName("UTF8")));
    os.writeBytes(CRLF);
  }

  public String toString() {
    return status;
  }
}
