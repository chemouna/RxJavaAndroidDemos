package com.mounacheikhna.rxdemos;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by cheikhnamouna on 11/8/15.
 */
public class TasksRunDemo {

  public static void main(String[] args) {
    Observable.concat(Observable.from(new BaseTask[] { new Task1(), new Task2(), new Task3() })
        .map(baseTask -> {
          baseTask.run();
          return baseTask.obs;
        }))
        .toBlocking().forEach(System.out::println);
  }

  enum TaskEvent {
    NEW_INTENT //,
    //NEXT_VALUE //ignoring it for now
  }

  static abstract class BaseTask {
    public PublishSubject<TaskEvent> obs = PublishSubject.create();
    abstract void run();
  }

  static class Task1 extends BaseTask {
    @Override void run() {
      System.out.println("Task1 running ");
      try {
        Thread.sleep(100);
        obs.onNext(TaskEvent.NEW_INTENT);
        Thread.sleep(100);
      } catch (Exception e) {
      }
      System.out.println("Task1 completed");
      obs.onCompleted();
    }
  }

  static class Task2 extends BaseTask {
    @Override void run() {
      System.out.println("Task2 running ");
      try {
        Thread.sleep(200);
      } catch (Exception e) {
      }
      System.out.println("Task2 completed");
      obs.onCompleted();
    }
  }

  static class Task3 extends BaseTask {
    @Override void run() {
      System.out.println("Task3 running ");
      try {
        Thread.sleep(400);
      } catch (Exception e) {
      }
      System.out.println("Task3 completed");
      obs.onCompleted();
    }
  }
}
