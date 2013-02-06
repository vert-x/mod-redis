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
package net.ml.vertx.mods.redis.commands.keys;

import java.util.List;
import java.util.concurrent.Future;

import net.ml.vertx.mods.redis.CommandContext;
import net.ml.vertx.mods.redis.commands.Command;
import net.ml.vertx.mods.redis.commands.CommandException;

import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

/**
 * KeysCommand
 * <p>
 * 
 * @author <a href="http://marx-labs.de">Thorsten Marx</a>
 */
public class KeysCommand extends Command {
	
	public static final String COMMAND = "keys";

	public KeysCommand () {
		super(COMMAND);
	}
	
	@Override
	public void handle(final Message<JsonObject> message, CommandContext context) throws CommandException {
		String pattern = getMandatoryString("pattern", message);
		checkNull(pattern, "pattern can not be null");
		

		try {

			final Future<List<String>> response = context.getConnection().keys(pattern);
			
			List<String> keys = response.get();
			
			JsonArray keys_json;
			if (keys != null && !keys.isEmpty()) {
				keys_json = new JsonArray(keys.toArray());
			} else {
				 keys_json = new JsonArray();
			}
			response(message, keys_json);

		} catch (Exception e) {
			sendError(message, e.getLocalizedMessage());
		}

	}
}
