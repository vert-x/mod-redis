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
package net.ml.vertx.mods.redis.commands.hashes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import net.ml.vertx.mods.redis.CommandContext;
import net.ml.vertx.mods.redis.commands.Command;
import net.ml.vertx.mods.redis.commands.CommandException;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * HMSetCommand
 * <p>
 * 
 * @author <a href="http://marx-labs.de">Thorsten Marx</a>
 */
public class HMSetCommand extends Command {

	public static final String COMMAND = "hmset";

	public HMSetCommand() {
		super(COMMAND);
	}

	@Override
	public void handle(final Message<JsonObject> message, CommandContext context) throws CommandException {
		
		String key = getMandatoryString("key", message);
		checkNull(key, "key can not be null");
		
		JsonObject fields = message.body.getObject("fields");
		
		checkNull(fields, "fields can not be null");
		
		try {
			
			final Map<String, String> fieldvalues = new HashMap<String, String>();
			
			for (String fn : fields.getFieldNames()) {
				Object fv = fields.getField(fn);
				if (!(fv instanceof String)) {
					throw new CommandException("only stringvalues are allowed for field values");
				}
				fieldvalues.put(fn, (String) fv);
			}
			
			
			Future<String> response = context.getConnection().hmset(key, fieldvalues);
			
			response(message, response.get());
		} catch (Exception e) {
			sendError(message, e.getLocalizedMessage());
		}

	}
}
