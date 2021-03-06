/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cex.ignite.client;


import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStore;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStoreFactory;
import org.apache.ignite.cache.store.jdbc.dialect.MySQLDialect;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cex.ignite.config.ConfigProperties;
import com.cex.ignite.model.CacheConfig;
import com.cex.ignite.model.Transaction;
import com.cex.ignite.model.TransactionKey;
import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

/**
 * create a server cache with persistence store
 */
public class CreateCache {
	
	private static Logger logger = LoggerFactory.getLogger(CreateCache.class);
	
	
    /**
     * Constructs and returns a fully configured instance of a {@link CacheJdbcPojoStoreFactory}.
     */
    public static class MySQLStoreFactory<K, V> extends CacheJdbcPojoStoreFactory<K, V> {
        /**
		 * 
		 */
		private static final long serialVersionUID = -305731694994951366L;

		/** {@inheritDoc} */
        @Override public CacheJdbcPojoStore<K, V> create() {
            setDialect(new MySQLDialect());
            
            MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
        	ds.setURL(ConfigProperties.getProperty(ConfigProperties.dbUrl));
        	ds.setUser(ConfigProperties.getProperty(ConfigProperties.dbUser));
        	ds.setPassword(ConfigProperties.getProperty(ConfigProperties.dbPwd));

            setDataSource(ds);

            return super.create();
        }
    }

    /**
     */
    public static void main(String[] args) throws IgniteException {
        logger.info(">>> Create cache...");

        // Start Ignite node.
        Ignition.setClientMode(true);
        try (Ignite ignite = Ignition.start(ConfigProperties.getProperty(ConfigProperties.configFile))) {
            // Configure cache store.
            CacheConfiguration<TransactionKey, Transaction> cfg =
                CacheConfig.cache(ConfigProperties.getProperty(ConfigProperties.cacheName), new MySQLStoreFactory<TransactionKey, Transaction>());
            
            cfg.setWriteBehindEnabled(true);
            cfg.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);

            // partitioned with 1 backup for each partition
            cfg.setCacheMode(CacheMode.PARTITIONED);
            cfg.setBackups(1);
            

            // create a server partitioned cache
            try (IgniteCache<TransactionKey, Transaction> cache = ignite.getOrCreateCache(cfg)) {
                logger.info("cache created...");
            }
            
        }
        
       
    }

   
    
}
