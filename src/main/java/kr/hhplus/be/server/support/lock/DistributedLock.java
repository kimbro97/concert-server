package kr.hhplus.be.server.support.lock;

import static kr.hhplus.be.server.support.lock.LockType.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
	String key();
	LockType type() default SIMPLE;
	long waitTime() default 5;
	long leaseTime() default 5;
	TimeUnit timeUnit() default TimeUnit.SECONDS;
}
