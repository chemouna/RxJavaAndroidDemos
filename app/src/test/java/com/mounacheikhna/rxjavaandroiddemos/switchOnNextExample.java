package com.mounacheikhna.rxjavaandroiddemos;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static junit.framework.Assert.assertEquals;

/**
 * Created by cheikhnamouna on 11/7/15.
 */
public class switchOnNextExample {

  public void example() {
    Observable.switchOnNext(Observable.interval(100, TimeUnit.MILLISECONDS)
            .map(i -> Observable.interval(30, TimeUnit.MILLISECONDS).map(i2 -> i)))
        .take(9)
        .subscribe(System.out::println);
  }

  @Test public void test() {
    TestSubscriber<Long> tester = new TestSubscriber<>();
    TestScheduler scheduler = Schedulers.test();

    Observable.switchOnNext(Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
            .map(i -> Observable.interval(30, TimeUnit.MILLISECONDS, scheduler).map(i2 -> i)))
        .distinctUntilChanged()
        .subscribe(tester);

    scheduler.advanceTimeBy(500, TimeUnit.MILLISECONDS);
    tester.assertReceivedOnNext(Arrays.asList(0L, 1L, 2L, 3L));
    tester.assertNoErrors();
    assertEquals(tester.getOnCompletedEvents().size(), 0);
  }
}