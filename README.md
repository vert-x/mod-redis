Redis busmod for Vert.x
=============================

### Redis-Client

This busmod allows provides access to a redis server. To use this busmod you must be have a redis server running on your network.

This is a worker busmod and must be started as a worker verticle.

#### Dependencies

This busmod requires a redis server to be available on the network.

#### Name

The module name is `redis-client`.

#### Configuration

The redis-client busmod takes the following configuration:

    {
        "address": <address>,
        "host": <host>,
        "port": <port>
    }
    
For example:

    {
        "address": "test.my_redisclient",
        "host": "192.168.1.100",
        "port": 6379,
    }        
    
Let's take a look at each field in turn:

* `address` The main address for the busmod. Every busmod has a main address. Defaults to `redis-client`.
* `host` Host name or ip address of the redis serve. Defaults to `localhost`.
* `port` Port at which the redis server is listening. Defaults to `27017`.

#### Usage

For example:
```javascript
eb.send('vertx.redis-client', {command: "set", key: 'name', value: 'vertx'});
```

The call consists of two parts, the redis command and the parameters for the command:

command : the redis command that should be called

Parameters
key : key parameter
value : value parameter

The Parameters are command specific and are mostly named after the official redis command documentation (http://redis.io/commands).


The error response looks like this:
	{
        "status": "ok",
        "value": <value>
    }
	
Value is the value returned by the redis server (http://redis.io/topics/protocol).

The error response looks like this:
    {
        "status": "error",
        "message": <message>
    }

Message is just the error message thrown by jedis.
	
	
#### Commands

The busmod supports the following redis commands

##### Connection
http://redis.io/commands#connection

###### Select

Change the selected database for the current connection

Command : select
Parameter : index (int)

Return : String (Status reply)

Example request:
    {
        command : "select",
        index : 1
    }
Example response:
    {
        status : "ok",
        value : "ok"
    }


##### Keys
http://redis.io/commands#generic

###### Exists

Determine if a key exists

Return : Boolean reply: 
	true if the key exists.
	false if the key does not exist.

Example request:
```javascript
{
	command : "exists",
	key : "key1"
}
```
Example response:
```javascript
{
	status : "ok",
	value : true
}
```
###### Del

Delete a key

Return : Integer reply: The number of keys that were removed

Example request:
```javascript
{
	command : "del",
	keys : [
		"key1"
	]
}
```
Example response:
```javascript
{
	status : "ok",
	value : 1
}
```	
###### Keys

Find all keys matching the given pattern.

Return : Multi-bulk reply: list of keys matching pattern.

Example request:
```javascript
{
	command : "keys",
	pattern : "h?llo"
}
```
Example response:
```javascript
{
	status : "ok",
	value : [
		"hello",
		"hallo"
	]
}
```	
Supported glob-style patterns:
* h?llo matches hello, hallo and hxllo
* h*llo matches hllo and heeeello
* h[ae]llo matches hello and hallo, but not hillo
Use \ to escape special characters

###### Expire

Set a key's time to live in seconds

Return : Integer reply:
	1 if the timeout was set.
	0 if key does not exist or the timeout could not be set.

Example request:
```javascript
{
	command : "expire",
	key : "the_key"
	seconds : 60
}
```
Example response:
```javascript
{
	status : "ok",
	value : 1
}
```
	
###### ExpireAt

Set the expiration for a key as a UNIX timestamp

Return : Integer reply:
	1 if the timeout was set.
	0 if key does not exist or the timeout could not be set.

Example request:
```javascript
{
	command : "expireat",
	key : "the_key"
	timestamp : 1293840000
}
```
Example response:
```javascript
{
	status : "ok",
	value : 1
}
```
	

	
###### Move

Move a key to another database

Return : Integer reply:
	1 if key was moved.
	0 if key was not moved.

Example request:
```javascript
{
	command : "move",
	key : "the_key"
	index : 1
}
```
Example response:
```javascript
{
	status : "ok",
	value : 1
}
```
###### Persist

Remove the expiration from a key

Return : Integer reply:
	1 if the timeout was removed.
	0 if key does not exist or does not have an associated timeout.

Example request:
```javascript
{
	command : "persist",
	key : "the_key"
}
```
Example response:
```javascript
{
	status : "ok",
	value : 1
}
```
	
###### RandomKey

Return a random key from the keyspace

Return : Bulk reply: the random key, or nil when the database is empty.

Example request:	
```javascript
{
	command : "randomkey"
}	
```
Example response:
```javascript
{
	status : "ok",
	value : "a_random_key"
}
```

###### Rename

Rename a key

Return : Status code reply

Example request:
```javascript
{
	command : "rename",
	key : "the_key"
	newkey : "new_key"
}
```
Example response:
```javascript
{
	status : "ok",
	value : "OK"
}
```
	
###### RenameNX

Rename a key, only if the new key does not exist

Return : Integer reply
	1 if key was renamed to newkey.
	0 if newkey already exists.

Example request:
```javascript
{
	command : "renamenx",
	key : "the_key"
	newkey : "new_key"
}
```
Example response:
```javascript
{
	status : "ok",
	value : 1
}
```

###### Sort

CURRENTLY NOT SUPPORTED

Sort the elements in a list, set or sorted set

Return : Multi-bulk reply: list of sorted elements.

Example request:
```javascript
{
	command : "sort",
	key : "the_key",
	alpha : true,
	order : "asc",
	by : "weight_*",
	start : 0,
	count : 10
}
```
order values:
	asc : sorted in ascending order
	desc : sorted in descending order
	

Example response:
```javascript
{
	status : "ok",
	value : [
		"value1",
		"value2"
	]
}
```
	
	
###### TTL

Get the time to live for a key

Return : Integer reply: TTL in seconds or -1 when key does not exist or does not have a timeout.

Example request:
```javascript
{
	command : "ttl",
	key : "the_key"
}
```
	

Example response:
```javascript
{
	status : "ok",
	value : 60
}
```

###### Type

Determine the type stored at key

Return : Status code reply: type of key, or none when key does not exist.

Example request:
```javascript
{
	command : "type",
	key : "the_key"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : "string"
}
```

posible types: string, list, set ...

##### Strings

http://redis.io/commands#string


###### Append

Append a value to a key

Return : Integer reply: the length of the string after the append operation.

Example request:
```javascript
{
	command : "append",
	key : "the_key",
	value : "to_append"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 16
}
```

###### DecrBy

Decrement the integer value of a key by the given number

Return : Integer reply: the value of key after the decrement

Example request:
```javascript
{
	command : "decrby",
	key : "the_key",
	decrement : 5
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 5
}
```




###### Decr

Decrement the integer value of a key by one

Return : Integer reply: the value of key after the decrement

Example request:
```javascript
{
	command : "decr",
	key : "the_key"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 6
}
```

###### GetBit

Returns the bit value at offset in the string value stored at key

Return : Integer reply: the bit value stored at offset.

Example request:
```javascript
{
	command : "getbit",
	key : "the_key",
	offset : 1
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 0
}
```

###### Get

Get the value of a key

Return : Bulk reply: the value of key, or nil when key does not exist.

Example request:
```javascript
{
	command : "get",
	key : "the_key"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : "the_value"
}
```

###### GetRange

Get a substring of the string stored at a key

Return : Bulk reply: the substring of the value

Example request:
```javascript
{
	command : "getrange",
	key : "the_key",
	start : 0,
	end : 10
}
```	

Example response:
```javascript
{
	status : "ok",
	value : "ing"
}
```

###### GetSet

Set the string value of a key and return its old value

Return : Bulk reply: the old value stored at key, or nil when key did not exist.

Example request:
```javascript
{
	command : "getset",
	key : "the_key",
	value : "new_value"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : "old_value"
}
```

###### IncrBy

Increment the integer value of a key by the given amount

Return : Integer reply: the value of key after the increment

Example request:
```javascript
{
	command : "incrby",
	key : "the_key",
	increment : 5
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 16
}
```

###### Incr

Increment the integer value of a key by one

Return : Integer reply: the value of key after the increment

Example request:
```javascript
{
	command : "incr",
	key : "the_key"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 17
}
```

###### MGet

Get the values of all the given keys

Return : Multi-bulk reply: list of values at the specified keys.

Example request:
```javascript
{
	command : "append",
	key : [
		"key_1", "key_2"
	]
}
```	

Example response:
```javascript
{
	status : "ok",
	value : [
		"value_1", "value_2"
	]
}
```

###### MSet

Set multiple keys to multiple values

Return : Status code reply: always OK since MSET can't fail.

Example request:
```javascript
{
	command : "mset",
	keyvalues : {
		"key_1" : "value_1",
		"key_2" : "value_2"
	}
}
```	

Example response:
```javascript
{
	value : "OK"
	status : "ok",
}
```


###### SetBit

Sets or clears the bit at offset in the string value stored at key

Return : Integer reply: the original bit value stored at offset.

Example request:
```javascript
{
	command : "setbit",
	key : "the_key",
	offset : 5,
	value: 1
}
```	

Example response:
```javascript
{
	status : "ok",
	value : false
}
```


###### Set

Set the string value of a key

Return : Status code reply: always OK since SET can't fail.

Example request:
```javascript
{
	command : "set",
	key : "the_key",
	value : "the_value"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : "OK"
}
```


###### SetEX

Set the value and expiration of a key

Return : Status code reply

Example request:
```javascript
{
	command : "setex",
	key : "the_key",
	value : "the_value"
	seconds : 60
}
```	

Example response:
```javascript
{
	status : "ok",
	value : OK
}
```


###### SetNX

Set the value of a key, only if the key does not exist

Return : Integer reply, specifically:
* 1 if the key was set
* 0 if the key was not set

Example request:
```javascript
{
	command : "setnx",
	key : "the_key",
	value : "the_value"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 1
}
```


###### SetRange

Overwrite part of a string at key starting at the specified offset

Return : Integer reply: the length of the string after it was modified by the command.

Example request:
```javascript
{
	command : "setrange",
	key : "the_key",
	value : "the_value",
	offset : 5
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 14
}
```


###### StrLen

Get the length of the value stored in a key

Return : Integer reply: the length of the string at key, or 0 when key does not exist.

Example request:
```javascript
{
	command : "strlen",
	key : "the_key"
}
```	

Example response:
```javascript
{
	status : "ok",
	value : 16
}
```

##### Lists

###### BLPop
Remove and get the first element in a list, or block until one is available

###### BRPop
Remove and get the last element in a list, or block until one is available

###### LIndex
Get an element from a list by its index

###### LInsert
Insert an element before or after another element in a list

###### LLen
Get the length of a list

###### LPop
Remove and get the first element in a list

###### LPush
Append one or multiple values to a list

###### LPushX
Prepend a value to a list, only if the list exists

###### LRange
Get a range of elements from a list

###### LRem
Remove elements from a list

###### LSet
Set the value of an element in a list by its index

###### LTrim
Trim a list to the specified range

###### RPop
Remove and get the last element in a list

###### RPopLPush
Remove the last element in a list, append it to another list and return it

###### RPush
Append one or multiple values to a list

###### RPushX
Append a value to a list, only if the list exists

##### Sets
http://redis.io/commands#set

###### SAdd
Add one or more members to a set

###### SCard
Get the number of members in a set

###### SDiff
Subtract multiple sets

###### SDiffStore
Subtract multiple sets and store the resulting set in a key

###### SInter
Intersect multiple sets

###### SInterStore
Intersect multiple sets and store the resulting set in a key

###### SIsMember
Determine if a given value is a member of a set

###### SMembers
Get all the members in a set

###### SMove
Move a member from one set to another

###### SPop
Remove and return a random member from a set

###### SRandMember
Get a random member from a set

###### SRem
Remove one or more members from a set

###### SUnion
Add multiple sets

###### SUnionStore
Add multiple sets and store the resulting set in a key

##### SortedSets
http://redis.io/commands#set

###### ZAdd
Add one or more members to a sorted set, or update its score if it already exists

###### ZCard
Get the number of members in a sorted set

###### ZCount
Count the members in a sorted set with scores within the given values

###### ZIncrBy
Increment the score of a member in a sorted set

###### ZInterStore
Intersect multiple sorted sets and store the resulting sorted set in a new key

###### ZRangeByScore
Return a range of members in a sorted set, by score

###### ZRange
Return a range of members in a sorted set, by index

###### ZRank
Determine the index of a member in a sorted set

###### ZRem
Remove one or more members from a sorted set

###### ZRemRangeByRank
Remove all members in a sorted set within the given indexes

###### ZRemRangeByScore
Remove all members in a sorted set within the given scores

###### ZRevRangeByScore
Return a range of members in a sorted set, by score, with scores ordered from high to low

###### ZRevRange
Return a range of members in a sorted set, by index, with scores ordered from high to low

###### ZRevRank
Determine the index of a member in a sorted set, with scores ordered from high to low

###### ZScore
Get the score associated with the given member in a sorted set

###### ZUnionStore
Add multiple sorted sets and store the resulting sorted set in a new key
