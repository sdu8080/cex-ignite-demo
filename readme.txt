
Note:
1. readthrough and writeafter support
2. when DB is down, readthrough will work if the record is found by the record key, write will continue to work, 
	once the DB is backup, the new records will be synced to DB