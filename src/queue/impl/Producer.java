package queue.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Producer implements Callable<Integer> {
	private CountDownLatch latch;
	private ReentrantReadWriteLock lock;
	private List<List<Integer>> unsortedList;
	private int pindex;

	public Producer(List<List<Integer>> unsortedList,CountDownLatch latch,ReentrantReadWriteLock lock,int pindex) {
		this.unsortedList = unsortedList;
		this.latch=latch;
		this.pindex=pindex;
		this.lock=lock;
	}

	@Override
	public Integer call() throws Exception {
		try {
			generateNumbers();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		finally {
			latch.countDown();
		}

		return null;
	}

	private void generateNumbers() throws InterruptedException {
		List<Integer> list=new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			for(int j=0;j<10;j++) {
				try {
					lock.writeLock().lock();
					int val=ThreadLocalRandom.current().nextInt(100);
					list.add(val);
					unsortedList.add(pindex,list);
					System.out.println(pindex+"\t"+ val);
				}catch(Exception e) {
					e.printStackTrace();
				}
				finally {
					//				System.out.println("Writing to Producer "+pindex+"\t"+ val );
					lock.writeLock().unlock();
				}
			}
		}
//		unsortedList.add(pindex,list);
	}

}
