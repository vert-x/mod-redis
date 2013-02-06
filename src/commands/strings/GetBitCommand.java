/*
 * Copyright 2011-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ml.vertx.mods.redis.commands.strings;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.ml.vertx.mods.redis.CommandContext;
import net.ml.vertx.mods.redis.commands.Command;
import net.ml.vertx.mods.redis.commands.CommandException;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * GetBitCommand
 * <p>
 * 
 * @author <a href="http://marx-labs.de">Thorsten Marx</a>
 */
public class GetBitCommand extends Command {
	
	public static final String COMMAND = "getbit";

	public GetBitCommand () {
		super(COMMAND);
	}
	
	@Override
	public void handle(final Message<JsonObject> message, CommandContext context) throws CommandException {
		String key = getMandatoryString("key", message);
		checkNull(key, "key can not be null");		

		Number offset = message.body.getNumber("offset");
		checkNull(offset, "offset can not be null");
		
		
		try {
			final Future<Long> value = context.getConnection().getbit(key, offset.longValue());
			
			response(message, value.get());
			
			vertx.runOnLoop(new Handler<Void>() {
				
				@Override
				public void handle(Void event) {
					try {
						response(message, value.get());
					} catch (Exception e) {
						sendError(message, e.getLocalizedMessage());
					}
				}
			});
		} catch (Exception e) {
			sendError(message, e.getLocalizedMessage());
		}

	}
}
