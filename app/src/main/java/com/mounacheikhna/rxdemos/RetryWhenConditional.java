package com.mounacheikhna.rxdemos;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import rx.Observable;
import rx.Subscriber;

public class RetryWhenConditional {

  //https://gist.github.com/benjchristensen/fde2e7d2dad2c746a449
  public static void main(String[] args) {

    AtomicInteger count = new AtomicInteger();
    Observable.create((Subscriber<? super String> s) -> {
      if (count.getAndIncrement() == 0) {
        s.onError(new RuntimeException("always fails"));
      } else {
        s.onError(new IllegalArgumentException("user error"));
      }
    }).retryWhen(attempts -> {
      return attempts.flatMap(n -> {
        if (n instanceof IllegalArgumentException) {
          System.out.println("don't retry on IllegalArgumentException... allow failure");
          return Observable.error(n);
        } else {
          System.out.println(n + " => retry after 1 second");
          return Observable.timer(1, TimeUnit.SECONDS);
        }
      });
    })
        .toBlocking().forEach(System.out::println);
  }

}
