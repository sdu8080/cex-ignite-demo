
Note:
1. the project tests ignite read-through and write-after features
2. if DB is down, read-through will work if the record is found by the record key, write will continue to work, 
	once the DB is backup, the new records will be synced to DB.  Read-through with missed search key will cause 
	connection error 
3. use ignite config file under examples/config/example-ignite.xml
4. use MYSQL DB
5. use sql/tables.sql to create table
6. use bin/ignite-schema-import.sh to generate data models and CacheConfig classes (already done)
7. to run, inside eclipse, first run StartNode, then CreateCache, to test, run DemoMain

performance results with record size 10000

2016-02-03 14:31:13 [main] DEBUG com.cex.ignite.client.DemoMain - 
+ Profiler [Demo]
|-- elapsed time           [remove cache] 106458794 nanoseconds.
|-- elapsed time                  [write] 2748254827 nanoseconds.
|-- elapsed time              [write end]    181605 nanoseconds.
|-- elapsed time             [read cache] 1218535450 nanoseconds.
|-- elapsed time               [read end]     85453 nanoseconds.
|-- elapsed time          [random update] 3078110111 nanoseconds.
|-- elapsed time             [update end]         0 nanoseconds.
|-- Total                          [Demo]         0 nanoseconds.