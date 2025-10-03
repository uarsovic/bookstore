package rs.ua.bookstore.logging

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.stereotype.Component

@Aspect
@Component
@EnableAspectJAutoProxy
class LoggingAspect {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(LoggingAspect::class.java)
    }

    /**
     * Log before every method enter
     *
     * @param joinPoint join point where aspect will be executed
     */
    @Before(value = "@within(org.springframework.stereotype.Service) || @within(Loggable) || @annotation(Loggable)")
    fun logEnterMethod(joinPoint: JoinPoint) {
        val methodName = joinPoint.signature.toShortString()
        if (joinPoint.args != null && joinPoint.args.size > 0) {
            log.debug("Enter: {}; args = {}", methodName, joinPoint.args)
        } else {
            log.debug("Enter: {}", methodName)
        }
    }

    /**
     * Log every return method
     *
     * @param joinPoint join point where aspect will be executed
     * @param result    value of a method return
     */
    @AfterReturning(
        value = "@within(org.springframework.stereotype.Service) || @within(Loggable) || @annotation(Loggable)",
        returning = "result"
    )
    fun logReturnResult(joinPoint: JoinPoint, result: Any?) {
        val methodName = joinPoint.signature.toShortString()
        if (result == null) {
            log.debug("Exit {}", methodName)
        } else {
            log.debug("Exit {}; return = {}", methodName, result)
        }
    }
}