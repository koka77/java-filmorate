package ru.yandex.practicum.filmorate.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.feed.FeedService;

@Aspect
@Component
@Slf4j
public class FeedAspect {

    private final FeedService feedService;

    public FeedAspect(FeedService feedService) {
        this.feedService = feedService;
    }

    @AfterReturning(pointcut = "updateControllerMethod() || addControllerMethod() || removeControllerMethod()|| reviewAllFeedMethod() || likeControllerMethod()", returning = "val")
    public void afterOperationAspect(JoinPoint jp, Object val) {

        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object[] parameters = jp.getArgs();
        String methodName = methodSignature.getName();

        if (val == null || methodSignature.getReturnType() == Review.class) {
            feedService.addFeed(methodName, parameters);
            log.info("был запущен метод : {} \r\n возвращаемое значение: {}", methodName, methodSignature.getReturnType());
        }
    }

    @AfterReturning(pointcut = "reviewUpdateMethod()", returning = "val")
    public void afterUpdateReviewAspect(JoinPoint jp, Object val) {
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Object[] parameters = jp.getArgs();
        String methodName = methodSignature.getName();
        feedService.updateFeedByEventId(parameters[0]);
    }


    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.add*(..))")
    private void addControllerMethod() {
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.update*(..))")
    private void updateControllerMethod() {
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.remove*(..))")
    private void removeControllerMethod() {
    }


    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.*.*Like(..))")
    private void likeControllerMethod() {
    }




    @Pointcut("reviewCreateMethod() || reviewDeleteMethod()")
    private void reviewAllFeedMethod() {
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.ReviewController.create(..))")
    private void reviewCreateMethod() {
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.ReviewController.create(..))")
    private void reviewDeleteMethod() {
    }

    @Pointcut("execution(public * ru.yandex.practicum.filmorate.controller.ReviewController.update(..))")
    private void reviewUpdateMethod() {
    }


}
