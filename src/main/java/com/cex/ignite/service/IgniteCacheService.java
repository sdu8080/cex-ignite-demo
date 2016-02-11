package com.cex.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicy;
import org.apache.ignite.configuration.NearCacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cex.ignite.config.ConfigProperties;
import com.cex.ignite.model.Transaction;
import com.cex.ignite.model.TransactionKey;

public class IgniteCacheService {
	
	private static Logger logger = LoggerFactory.getLogger(IgniteCacheService.class);

	// singleton instance
	public static IgniteCacheService instance = new IgniteCacheService();

	private Ignite ignite = null;

	private IgniteCache<TransactionKey, Transaction> cache = null;
	
	private IgniteCache<TransactionKey, Transaction> nearCache = null;

	private boolean initialized = false;

	private IgniteCacheService() {
	}

	public synchronized void initialize(boolean clientMode) {
		if (!initialized) {
			Ignition.setClientMode(clientMode);
			try {
				ignite = Ignition.start(ConfigProperties
						.getProperty(ConfigProperties.configFile));
				initTxnCache(clientMode);
			} catch (IgniteException e) {
				e.printStackTrace();
			}
			initialized = true;
		}
	}

	private void initTxnCache(boolean clientMode) {
		try {
			String cacheName = ConfigProperties.getProperty(ConfigProperties.cacheName);
			
			int nearCacheSize = Integer.parseInt(ConfigProperties.getProperty("NEARCACHE_SIZE"));
			
			NearCacheConfiguration<TransactionKey, Transaction> nearCfg = new NearCacheConfiguration<>();

			nearCfg.setNearEvictionPolicy(new LruEvictionPolicy<TransactionKey, Transaction>(nearCacheSize));

			// create clisnt side near cache before get the server txn cache
			if(clientMode){
				nearCache = ignite.getOrCreateNearCache(cacheName, nearCfg);
			}
			
			// get the server side txn cache
			cache = ignite.getOrCreateCache(cacheName);
			
		} catch (Exception e) {
			logger.error("failed to create ignite cache.", e);
		}

	}

	public IgniteCache<TransactionKey, Transaction> getTxnCache() {
		return cache;
	}
	
	public IgniteCache<TransactionKey, Transaction> getNearCache() {
		return nearCache;
	}

	public Ignite getIgnite() {
		return ignite;
	}

	public void finish() {

		if (nearCache != null) {
			try {
				nearCache.close();
			} catch (Exception e) {
				logger.error("failed to close the nearCache", e);
			}
		}
		
		if (cache != null) {
			try {
				cache.close();
			} catch (Exception e) {
				logger.error("failed to close the txnCache", e);
			}
		}

		if (ignite != null) {
			try {
				ignite.close();
			} catch (IgniteException e) {
				logger.error("failed to close the ignite instance", e);
			}
		}
	}

}
