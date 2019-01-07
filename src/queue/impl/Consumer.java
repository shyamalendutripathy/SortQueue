package queue.impl;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Consumer implements Callable<Integer>{
	List<List<Integer>> unsortedList;
	List<List<Integer>> sortedList;
	private CountDownLatch latch;
	private ReentrantReadWriteLock lock;
	private int cIndex;

	public Consumer(List<List<Integer>> unsortedList,List<List<Integer>> sortedList,CountDownLatch latch,ReentrantReadWriteLock lock,int cIndex) {
		this.unsortedList=unsortedList;
		this.sortedList=sortedList;
		this.latch=latch;
		this.lock=lock;
		this.cIndex=cIndex;
	}

	@Override
	public Integer call() throws Exception {
		List<Integer> entry=null;
		try {
			while (true) {
				Thread.sleep(10);
				lock.writeLock().lock();
				entry=unsortedList.get(cIndex);
				if(entry==null || entry.isEmpty()) {
					continue;
				}
				Collections.sort(entry);
				sortedList.add(cIndex,entry);
			System.out.println(cIndex);
			entry.stream().forEach(s-> System.out.print(s+" "));
			System.out.println();
			latch.countDown();
			lock.writeLock().unlock();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		finally {
//			lock.writeLock().unlock();
		}
		return 1;
	}
}
