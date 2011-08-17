package org.zebra.search.crawler.common;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter {
	private AtomicLong counter = new AtomicLong(0l);
	/**
	 * constructor
	 */
	public AtomicCounter() {
	}
	/**
	 * set
	 */
	public final long get() {
		return this.counter.get();
	}
	/**
	 * set
	 */
	public final void set(long newValue) {
		this.counter.set(newValue);
	}
	public final long getAndIncrement() {
		return this.counter.getAndIncrement();
	}
	public final long incrementAndGet() {
		return this.counter.incrementAndGet();
	}
	public final long getAndDecrement() {
		return this.counter.getAndDecrement();
	}
	public final long decrementAndGet() {
		return this.counter.decrementAndGet();
	}
	public final long add(long delta) {
		return this.counter.getAndAdd(delta);
	}
	public String toString() {
		return this.counter.toString();
	}
}