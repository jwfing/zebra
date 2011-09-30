package org.zebra.search.crawler.util;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounter {
	private AtomicInteger counter = new AtomicInteger(0);
	/**
	 * constructor
	 */
	public AtomicCounter() {
	}
	/**
	 * set
	 */
	public final int get() {
		return this.counter.get();
	}
	/**
	 * set
	 */
	public final void set(int newValue) {
		this.counter.set(newValue);
	}
	public final int getAndIncrement() {
		return this.counter.getAndIncrement();
	}
	public final int incrementAndGet() {
		return this.counter.incrementAndGet();
	}
	public final int getAndDecrement() {
		return this.counter.getAndDecrement();
	}
	public final int decrementAndGet() {
		return this.counter.decrementAndGet();
	}
	public final int add(int delta) {
		return this.counter.getAndAdd(delta);
	}
	public String toString() {
		return this.counter.toString();
	}
}