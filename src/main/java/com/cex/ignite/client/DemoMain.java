package com.cex.ignite.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.lang.IgniteCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import com.cex.ignite.model.Transaction;
import com.cex.ignite.model.TransactionKey;
import com.cex.ignite.service.IgniteCacheService;
import com.cex.ignite.service.ProcessingService;

public class DemoMain {
	
	private static Logger logger = LoggerFactory.getLogger(DemoMain.class);
	
	static AtomicInteger counter = new AtomicInteger(0);
	
	static Profiler p = null;
	

	public static void main(String[] args) {
		
		logger.info("start demo...");
		p = new Profiler("Demo");
		p.setLogger(logger);
		int size = 10000;
		// initialize
		IgniteCacheService service = IgniteCacheService.instance;
		service.initialize();
		// demo tests
		remoteProcessing(service);
		populateDate(service, size);
		readData(service, size);
		updateData(service, size);
		nearCache(service, size);
		getCacheSize(service);
		// end tests
		service.finish();  
		// log performance data
		p.log();
	}

	private static void populateDate(IgniteCacheService service, int size) {
		IgniteCache<TransactionKey, Transaction> cache = service.getTxnCache();
		
		logger.info("start populateData...");
		p.start("remove cache");
		cache.removeAll();
		p.start("write");
		for (int i = 0; i < size; i++) {
			TransactionKey key = new TransactionKey(Integer.toString(i));
			Transaction t = new Transaction(Integer.toString(i), "adfasdfasdfasdf", "channel-1560", "upc-1233534", 
					100.50, "ACT", new Timestamp(System.currentTimeMillis()), "comments "+i);
			cache.put(key, t);
		}
		p.start("write end");
		
	}
	
	private static void readData(IgniteCacheService service, int size) {
		IgniteCache<TransactionKey, Transaction> cache = service.getTxnCache();
		
		logger.info("start readData...");
		Transaction txn2 = null;
		p.start("read cache");
		for(int k = size; k>0; k--){
			txn2 = cache.get(new TransactionKey(Integer.toString(k)));
		}
		logger.info("txn = "+txn2.toString());
		p.start("read end");
	}
	
	private static void updateData(IgniteCacheService service, int size) {
		IgniteCache<TransactionKey, Transaction> cache = service.getTxnCache();

		logger.info("start updateData...");
		p.start("random update");
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10000; i++) {
			Runnable worker = new WorkerThread(cache, size);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		p.start("update end");

	}
	
	static class WorkerThread implements Runnable{
		
		IgniteCache<TransactionKey, Transaction> cache = null;
		int n = 0;
		
		public WorkerThread(IgniteCache<TransactionKey, Transaction> cache, int n){
			this.cache = cache;
			this.n = n;
		}

		@Override
		public void run() {
			int randomNum = 0 + (int)(Math.random() * n); 
			
			TransactionKey key = new TransactionKey(Integer.toString(randomNum));
			Transaction t = cache.get(key);
			t.setTransactionTime(new Timestamp(System.currentTimeMillis()) );
			t.setRemark("updated...");
			cache.put(key, t);
			counter.incrementAndGet();
			
		}
		
	}

	private static void remoteProcessing(IgniteCacheService service) {
		Collection<IgniteCallable<Integer>> calls = new ArrayList<>();

		// Iterate through all the words in the sentence and create Callable
		// jobs.
		for (final String word : "Count characters using callable foo bar."
				.split(" ")) {
			ProcessingService myStr = new ProcessingService(word);
			calls.add(myStr::process);
		}

		// Execute collection of Callables on the grid.
		Collection<Integer> res = service.getIgnite().compute().call(calls);

		// Add up all the results.
		int sum = res.stream().mapToInt(Integer::intValue).sum();

		logger.info("Total number of characters is '" + sum + "'.");
	}
	
	private static void nearCache(IgniteCacheService service, int size) {
		
		logger.info("start near cache...");
		p.start("nearCache");
		Transaction t = null;
		for(int i = 0; i< 200; i++){
			TransactionKey key = new TransactionKey(Integer.toString(i));
			t = service.getNearCache().get(key);
		}
		logger.info(t.toString());
		
		p.start("nearCache end");
		
	}
	
	private static void getCacheSize(IgniteCacheService service){
		int size1 = service.getNearCache().size(CachePeekMode.NEAR);
		logger.info("near cache size = "+size1);
		int size2 = service.getTxnCache().size(CachePeekMode.ALL);
		logger.info("total cache size = "+size2);
		int size3 = service.getTxnCache().size(CachePeekMode.PRIMARY);
		logger.info("partitioned cache size = "+size3);
		int size4 = service.getTxnCache().size(CachePeekMode.BACKUP);
		logger.info("backup cache size = "+size4);
		int size5 = service.getTxnCache().size(CachePeekMode.ONHEAP);
		logger.info("onheap cache size = "+size5);
		int size6 = service.getTxnCache().size(CachePeekMode.OFFHEAP);
		logger.info("offheap cache size = "+size6);
		int size7 = service.getTxnCache().size(CachePeekMode.SWAP);
		logger.info("swap cache size = "+size7);
		
	}

}
