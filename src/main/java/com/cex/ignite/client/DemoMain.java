package com.cex.ignite.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.lang.IgniteCallable;

import com.cex.ignite.model.Transaction;
import com.cex.ignite.model.TransactionKey;
import com.cex.ignite.service.IgniteCacheService;
import com.cex.ignite.service.ProcessingService;

public class DemoMain {

	public static void main(String[] args) {
		IgniteCacheService service = IgniteCacheService.instance;
		service.initialize();
		igniteCrud(service);
		remoteProcessing(service);
		service.finish();  
	}

	private static void igniteCrud(IgniteCacheService service) {
		IgniteCache<TransactionKey, Transaction> cache = service.getTxnCache();
		
		long t1 = System.currentTimeMillis();
		for (int i = 1; i <= 10020; i++) {
			TransactionKey key = new TransactionKey(Integer.toString(i));
			Transaction t = new Transaction(Integer.toString(i), "adfasdfasdfasdf", "channel-1560", "upc-1233534", 
					100.50, "ACT", new Timestamp(System.currentTimeMillis()), "comments "+i);
			cache.put(key, t);
		}
		long t2 = System.currentTimeMillis();
		long t3 = (t2-t1)/1000;
		
		System.out.println("time used write= "+t3+"seconds");
		
		
		Transaction txn2 = null;
		t1 = System.currentTimeMillis();
		for(int k = 1; k<9999; k++){
			txn2 = cache.get(new TransactionKey(Integer.toString(k)));
			if(k==0){
				System.out.println(txn2);
			}
		}
		t2 = System.currentTimeMillis();
		t3 = (t2-t1)/1000;
		System.out.println("time used read= "+t3+"seconds");
		System.out.println("txn = "+txn2.toString());
	}
	
	public static void remoteProcessing(IgniteCacheService service) {
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

		System.out.println("Total number of characters is '" + sum + "'.");
	}

}
