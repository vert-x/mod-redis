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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import net.ml.vertx.mods.redis.CommandContext;
import net.ml.vertx.mods.redis.commands.Command;
import net.ml.vertx.mods.redis.commands.CommandException;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * MSetNXCommand
 * <p>
 * 
 * @author <a href="http://marx-labs.de">Thorsten Marx</a>
 */
public class MSetNXCommand extends Command {

	public static final String COMMAND = "msetnx";

	public MSetNXCommand() {
		super(COMMAND);
	}

	@Override
	public void handle(final Message<JsonObject> message, CommandContext context) throws CommandException {
		JsonObject keyvalues = message.body.getObject("keyvalues");
		
		checkNull(keyvalues, "keyvalues can not be null");
		
		try {
			
			Map<String, String> keyvalue = new HashMap<String, String>();
			
		
			for (String fn : keyvalues.getFieldNames()) {
				Object fv = keyvalues.getField(fn);
				if (!(fv instanceof String)) {
					throw new CommandException("only stringvalues are allowed for field values");
				}
				keyvalue.put(fn, (String) fv);
			}
			
			final Future<Boolean> response = context.getConnection().msetnx(keyvalue);
			
			response(message, response.get());
		} catch (Exception e) {
			sendError(message, e.getLocalizedMessage());
		}

	}
}
