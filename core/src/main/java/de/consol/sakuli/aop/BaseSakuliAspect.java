package de.consol.sakuli.aop;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tschneck
 *         Date: 23.09.14
 */
public abstract class BaseSakuliAspect {
    /**
     * @return the {@link Logger} for the assigned joinPoint.
     */
    protected Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
    }

    protected String getClassAndMethodAsString(JoinPoint joinPoint) {
        return String.format("%s.%s()",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }
}
