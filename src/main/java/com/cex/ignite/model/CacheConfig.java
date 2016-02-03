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

package com.cex.ignite.model;

import java.sql.*;
import java.util.*;

import org.apache.ignite.cache.*;
import org.apache.ignite.cache.store.jdbc.*;
import org.apache.ignite.configuration.*;

/**
 * CacheConfig definition.
 *
 * Code generated by Apache Ignite Schema Import utility: 02/01/2016.
 */
public class CacheConfig {
    /**
     * Create JDBC type for transaction.
     *
     * @param cacheName Cache name.
     * @return Configured JDBC type.
     */
    private static JdbcType jdbcTypeTransaction(String cacheName) {
        JdbcType jdbcType = new JdbcType();

        jdbcType.setCacheName(cacheName);
        jdbcType.setDatabaseSchema("test");
        jdbcType.setDatabaseTable("transaction");
        jdbcType.setKeyType("com.cex.ignite.model.TransactionKey");
        jdbcType.setValueType("com.cex.ignite.model.Transaction");

        // Key fields for transaction.
        Collection<JdbcTypeField> keys = new ArrayList<>();
        keys.add(new JdbcTypeField(Types.VARCHAR, "transaction_id", String.class, "transactionId"));
        jdbcType.setKeyFields(keys.toArray(new JdbcTypeField[keys.size()]));

        // Value fields for transaction.
        Collection<JdbcTypeField> vals = new ArrayList<>();
        vals.add(new JdbcTypeField(Types.VARCHAR, "transaction_id", String.class, "transactionId"));
        vals.add(new JdbcTypeField(Types.VARCHAR, "card_no", String.class, "cardNo"));
        vals.add(new JdbcTypeField(Types.VARCHAR, "channel_id", String.class, "channelId"));
        vals.add(new JdbcTypeField(Types.VARCHAR, "upc", String.class, "upc"));
        vals.add(new JdbcTypeField(Types.DOUBLE, "load_value", double.class, "loadValue"));
        vals.add(new JdbcTypeField(Types.CHAR, "card_status", String.class, "cardStatus"));
        vals.add(new JdbcTypeField(Types.TIMESTAMP, "transaction_time", java.sql.Timestamp.class, "transactionTime"));
        vals.add(new JdbcTypeField(Types.VARCHAR, "remark", String.class, "remark"));
        jdbcType.setValueFields(vals.toArray(new JdbcTypeField[vals.size()]));

        return jdbcType;
    }

    /**
     * Create SQL Query descriptor for transaction.
     *
     * @return Configured query entity.
     */
    private static QueryEntity queryEntityTransaction() {
        QueryEntity qryEntity = new QueryEntity();

        qryEntity.setKeyType("com.cex.ignite.model.TransactionKey");
        qryEntity.setValueType("com.cex.ignite.model.Transaction");

        // Query fields for transaction.
        LinkedHashMap<String, String> fields = new LinkedHashMap<>();

        fields.put("transactionId", "String");
        fields.put("cardNo", "String");
        fields.put("channelId", "String");
        fields.put("upc", "String");
        fields.put("loadValue", "double");
        fields.put("cardStatus", "String");
        fields.put("transactionTime", "java.sql.Timestamp");
        fields.put("remark", "String");

        qryEntity.setFields(fields);

        // Indexes for transaction.
        Collection<QueryIndex> idxs = new ArrayList<>();

        idxs.add(new QueryIndex("transaction_id", true, "PRIMARY"));

        qryEntity.setIndexes(idxs);

        return qryEntity;
    }

    /**
     * Configure cache.
     *
     * @param cacheName Cache name.
     * @param storeFactory Cache store factory.
     * @return Cache configuration.
     */
    public static <K, V> CacheConfiguration<K, V> cache(String cacheName, CacheJdbcPojoStoreFactory<K, V> storeFactory) {
        if (storeFactory == null)
             throw new IllegalArgumentException("Cache store factory cannot be null.");

        CacheConfiguration<K, V> ccfg = new CacheConfiguration<>(cacheName);

        
        ccfg.setCacheStoreFactory(storeFactory);
        ccfg.setReadThrough(true);
        ccfg.setWriteThrough(true);



        // Configure JDBC types. 
        Collection<JdbcType> jdbcTypes = new ArrayList<>();

        jdbcTypes.add(jdbcTypeTransaction(cacheName));

        storeFactory.setTypes(jdbcTypes.toArray(new JdbcType[jdbcTypes.size()]));
        
        

        // Configure query entities. 
        Collection<QueryEntity> qryEntities = new ArrayList<>();

        qryEntities.add(queryEntityTransaction());

        ccfg.setQueryEntities(qryEntities);

        return ccfg;
    }
}
