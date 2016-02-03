
Note:
1. read-through and write-after support
2. when DB is down, read-through will work if the record is found by the record key, write will continue to work, 
	once the DB is backup, the new records will be synced to DB