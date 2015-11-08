package com.mounacheikhna.rxdemos;

import java.util.LinkedList;
import rx.Observable;
import rx.Subscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

/**
 * Created by cheikhnamouna on 11/8/15.
 */
public class TasksRunDemo {

  public static void main(String[] args) {
    /*Observable.concat(Observable.from(new BaseTask[] { new Task1(), new Task2(), new Task3() })
        .map(baseTask -> {
          baseTask.run();
          return baseTask.obs;
        }))
        .toBlocking().forEach(System.out::println);*/

    final LinkedList<BaseTask> tasks = new LinkedList<>();
    tasks.add(new Task1());
    tasks.add(new Task2());
    tasks.add(new Task3());
    final Runner runner = new Runner(tasks);
    runner.run();
  }

  public static <T> Observable.Transformer<T, T> takeNextAndUnsubscribe() {
    return observable -> {
      BehaviorSubject<T> subject = BehaviorSubject.create();
      Observable<T> source = observable.doOnNext(subject::onNext);
      return Observable
          .merge(source.takeUntil(subject), subject)
          .take(1);
    };
  }

  static class Runner {

    private final LinkedList<BaseTask> tasks;

    public Runner(LinkedList<BaseTask> tasks) {
      this.tasks = tasks;
    }

    void run() {
      tasks.poll().run()
        .compose(takeNextAndUnsubscribe())
        .concatWith(tasks.isEmpty() ? Observable.empty() : tasks.poll().run())
        .takeUntil(taskEvent -> {
          return tasks.isEmpty();
        })
        .subscribe(new Subscriber<TaskEvent>() {
          @Override public void onCompleted() {
            System.out.println(" run onCompleted ");
          }

          @Override public void onError(Throwable e) {
            System.out.println(" run onError "+ e);
          }

          @Override public void onNext(TaskEvent taskEvent) {
            System.out.println(" run onNext w "+ taskEvent);
          }
        });
    }

  }

  enum TaskEvent {
    NEW_INTENT,
    NEXT_VALUE
  }

  static abstract class BaseTask {
    public PublishSubject<TaskEvent> obs = PublishSubject.create();
    abstract Observable<TaskEvent> run();
  }

  static class Task1 extends BaseTask {
    @Override Observable<TaskEvent> run() {
      System.out.println("Task1 running");
      try {
        Thread.sleep(100);
        obs.onNext(TaskEvent.NEW_INTENT);
        Thread.sleep(100);
      } catch (Exception e) {
      }
      System.out.println("Task1 completed");
      obs.onCompleted();
      return obs;
    }
  }

  static class Task2 extends BaseTask {
    static int nb_values = 3;
    @Override Observable<TaskEvent> run() {
      System.out.println("Task2 running w values nb "+ nb_values);
      try {
        Thread.sleep(200);
        /*nb_values--;
        obs.onNext(TaskEvent.NEXT_VALUE);*/
      } catch (Exception e) {
      }
      System.out.println("Task2 completed");
      //if(nb_values == 0) {
        obs.onCompleted();
      //}
      return obs;
    }
  }

  static class Task3 extends BaseTask {
    @Override Observable<TaskEvent> run() {
      System.out.println("Task3 running ");
      try {
        Thread.sleep(400);
      } catch (Exception e) {
      }
      System.out.println("Task3 completed");
      obs.onCompleted();
      return obs;
    }
  }
}
