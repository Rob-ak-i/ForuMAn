package util;

import java.util.concurrent.TimeUnit;

public abstract class RunnableThread implements Runnable {
	
	protected int maxSleepTime;
	protected int ticks;
	protected Thread thread;
	protected boolean threadState;public boolean getThreadState() {return threadState;}
	public RunnableThread(int maxSleepTime) {
		this.maxSleepTime=maxSleepTime;
		ticks=0;
		threadState=false;
	}
	public abstract void runInner();
	@Override
	public void run() {long startProcess, durationMs;
		while(threadState) {
			startProcess=System.nanoTime();
			
			runInner();
			
			durationMs=TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startProcess);
			if (durationMs < maxSleepTime) {
				try {
					Thread.sleep(maxSleepTime - durationMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public synchronized void start() {
		if(threadState) {System.out.println("Warning: thread already started."); return;}
		thread = new Thread(this);
		thread.setName(this.getClass().getSimpleName());
		thread.start();
		threadState = true;
	}
	public synchronized void stop() {
		thread = null;
		threadState = false;
	}
}
