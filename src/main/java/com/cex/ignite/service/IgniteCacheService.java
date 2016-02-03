package com.cex.ignite.service;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;

import com.cex.ignite.config.ConfigProperties;
import com.cex.ignite.model.Transaction;
import com.cex.ignite.model.TransactionKey;

public class IgniteCacheService {

	public static IgniteCacheService instance = new IgniteCacheService();

	private Ignite ignite = null;

	private IgniteCache<TransactionKey, Transaction> cache = null;

	private boolean initialized = false;

	private IgniteCacheService() {
	}

	public synchronized void initialize() {
		if (!initialized) {
			Ignition.setClientMode(true);
			try {
				ignite = Ignition.start(ConfigProperties
						.getProperty(ConfigProperties.configFile));
				initTxnCache();
			} catch (IgniteException e) {
				e.printStackTrace();
			}
			initialized = true;
		}
	}

	private void initTxnCache() {
		try {
			cache = ignite.getOrCreateCache("TransactionCache");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public IgniteCache<TransactionKey, Transaction> getTxnCache() {
		return cache;
	}

	public Ignite getIgnite() {
		return ignite;
	}

	public void finish() {

		if (cache != null) {
			try {
				cache.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (ignite != null) {
			try {
				ignite.close();
			} catch (IgniteException e) {
				e.printStackTrace();
			}
		}
	}

}
