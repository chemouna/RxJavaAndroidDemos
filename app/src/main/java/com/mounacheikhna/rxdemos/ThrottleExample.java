package com.mounacheikhna.rxdemos;

import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by cheikhnamouna on 11/5/15.
 */
public class ThrottleExample {

  public static void main(String args[]) {
    // first item emitted in each time window
    hotStream().throttleFirst(500, TimeUnit.MILLISECONDS)
        .take(10)
        .toBlocking()
        .forEach(System.out::println);

    // last item emitted in each time window
    hotStream().throttleLast(500, TimeUnit.MILLISECONDS)
        .take(10)
        .toBlocking()
        .forEach(System.out::println);
  }

  //hot -> we need to subscribe to it for it to start emitting any items
  public static Observable<Integer> hotStream() {
    return Observable.create((Subscriber<? super Integer> s) -> {
      int i = 0;
      while (!s.isUnsubscribed()) {
        s.onNext(i++);
        try {
          // sleep for a random amount of time
          Thread.sleep((long) (Math.random() * 100));
        } catch (Exception e) {
          // do nothing
        }
      }
    }).subscribeOn(Schedulers.newThread());
  }
}
