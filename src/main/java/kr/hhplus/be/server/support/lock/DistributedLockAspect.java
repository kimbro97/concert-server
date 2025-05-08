package kr.hhplus.be.server.support.lock;

import static kr.hhplus.be.server.support.exception.BusinessError.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.support.lock.strategy.LockStrategy;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DistributedLockAspect {

	private static final String LOCK_PREFIX = "lock:";

	private final LockStrategyFactory lockStrategyFactory;

	@Around("@annotation(kr.hhplus.be.server.support.lock.DistributedLock)")
	public Object acquireLock(ProceedingJoinPoint joinPoint) throws Throwable {

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Object[] args = joinPoint.getArgs();

		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
		String lockKey = lockKeyParser(distributedLock.key(), method, args);
		String value = UUID.randomUUID().toString();
		LockStrategy strategy = lockStrategyFactory.getStrategy(distributedLock.type());

		long waitTime = distributedLock.waitTime();
		long leaseTime = distributedLock.leaseTime();
		TimeUnit timeUnit = distributedLock.timeUnit();

		boolean lockSuccess = strategy.tryLock(lockKey, value, waitTime, leaseTime, timeUnit);

		if (!lockSuccess) {
			throw LOCK_ACQUISITION_FAILED_ERROR.exception();
		}

		try {
			return joinPoint.proceed();
		} finally {
			strategy.unLock(lockKey, value);
		}
	}

	private String lockKeyParser(String lockKey, Method method, Object[] args) {

		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();

		Parameter[] parameters = method.getParameters();

		for (int i = 0; i < parameters.length; i++) {
			context.setVariable(parameters[i].getName(), args[i]);
		}

		Expression expression = parser.parseExpression(lockKey);
		return LOCK_PREFIX + expression.getValue(context, String.class);
	}
}
