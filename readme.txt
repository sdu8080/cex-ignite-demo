
Note:
1. the project tests read-through and write-after supports
2. when DB is down, read-through will work if the record is found by the record key, write will continue to work, 
	once the DB is backup, the new records will be synced to DB.  Read-through with missed search key will cause 
	connection error 
3. use ignite config file under examples/config/example-ignite.xml
4. use MYSQL DB
5. use sql/tables.sql to create table
6. use bin/ignite-schema-import.sh to generate data models and CacheConfig classes
7. to run, inside eclipse, first run StartNode, then CreateCache, to test, run DemoMain