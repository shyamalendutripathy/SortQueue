package queue.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainImpl {
	int BOUND = 500;
	int N_PRODUCERS = 5;
	int N_CONSUMERS = 5;
	private CountDownLatch latch=new CountDownLatch(N_PRODUCERS+N_CONSUMERS);
	private List<List<Integer>> unsortedList=new ArrayList<>(Collections.nCopies(5, null));
	private List<List<Integer>> sortedList=new ArrayList<>(Collections.nCopies(5, null));
	ReentrantReadWriteLock lock=new ReentrantReadWriteLock(true);
	private ExecutorService executor = Executors.newFixedThreadPool(10);
	private void launchProducers() {
		Producer prodFuture=null;
		//		Needs Executor service to restrict launching of multiple number of threads.
		for (int i = 0; i < N_PRODUCERS; i++) {
			try {
				prodFuture=new Producer(unsortedList,latch,lock,i);
				executor.submit(prodFuture);
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
			finally {
//				lock.unlock();
			}
		}
	}

	private void launchConsumers() {
		Consumer consumeFuture=null;
		//		Needs Executor service to restrict launching of multiple number of threads.
		for (int i = 0; i < N_CONSUMERS; i++) {
			try {
			consumeFuture=new Consumer(unsortedList,sortedList,latch,lock,i);
			executor.submit(consumeFuture);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		try {
			latch.await(5,TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		MainImpl impl=new MainImpl();
		impl.launchProducers();
		impl.launchConsumers();
		
		try {
			impl.latch.await(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		impl.executor.shutdown();
	}
}
