package org.zebra.common;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter {
	private AtomicLong counter = new AtomicLong(0l);
	public AtomicCounter() {
	}
	public final long get() {
		return this.counter.get();
	}
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