package com.mounacheikhna.rxdemos;

import android.text.TextUtils;
import com.github.davidmoten.rx.Transformers;
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
      return Observable.merge(source.takeUntil(subject), subject).take(1);
    };
  }

  enum TaskEvent {
    NEW_INTENT
  }

  static class Runner {

    private final LinkedList<BaseTask> tasks;

    public Runner(LinkedList<BaseTask> tasks) {
      this.tasks = tasks;
    }

    void run() {
      //attempt1();
      attempt2();
      //attempt3();
      //attempt4();
      //attemptWithInputOutput();
    }

    private void attemptWithInputOutput() {
      Observable.from(tasks).flatMap(BaseTask::run)
          .subscribe(new Subscriber<TaskEvent>() {
        @Override public void onCompleted() {
          System.out.println(" run onCompleted ");
        }

        @Override public void onError(Throwable e) {
          System.out.println(" run onError " + e);
        }

        @Override public void onNext(TaskEvent taskEvent) {
          System.out.println(" run onNext w " + taskEvent);
        }
      });
      ;
    }

    private void attempt4() {
      Observable.from(tasks)
          .flatMap(BaseTask::run)
          .compose(Transformers.bufferEmissions())
          .subscribe(new Subscriber<TaskEvent>() {
            @Override public void onCompleted() {
              System.out.println(" run onCompleted ");
            }

            @Override public void onError(Throwable e) {
              System.out.println(" run onError " + e);
            }

            @Override public void onNext(TaskEvent taskEvent) {
              System.out.println(" run onNext w " + taskEvent);
            }
          });
    }

    private void attempt3() {
      final Observable<TaskEvent> source = Observable.from(tasks).flatMap(BaseTask::run);
      BehaviorSubject<TaskEvent> subject = BehaviorSubject.create();
      Observable<TaskEvent> newSource = source.doOnNext(taskEvent -> {
        if (!TaskEvent.NEW_INTENT.equals(taskEvent)) {
          subject.onNext(taskEvent);
        }
      });

      Observable.merge(newSource.takeUntil(subject), subject)
          .subscribe(new Subscriber<TaskEvent>() {
            @Override public void onCompleted() {
              System.out.println(" run onCompleted ");
            }

            @Override public void onError(Throwable e) {
              System.out.println(" run onError " + e);
            }

            @Override public void onNext(TaskEvent taskEvent) {
              System.out.println(" run onNext w " + taskEvent);
            }
          });
    }

    private void attempt1() {
      tasks.poll()
          .run()
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
              System.out.println(" run onError " + e);
            }

            @Override public void onNext(TaskEvent taskEvent) {
              System.out.println(" run onNext w " + taskEvent);
            }
          });
    }

    private void attempt2() {
      Observable<TaskEvent> sourceObservable = Observable.from(tasks).flatMap(BaseTask::run);

      /*BehaviorSubject<TaskEvent> subject = BehaviorSubject.create();
      Observable<TaskEvent> source = sourceObservable.doOnNext(taskEvent -> {
        if(!TaskEvent.NEW_INTENT.equals(taskEvent) && !TaskEvent.NEXT_VALUE.equals(taskEvent)) {
          subject.onNext(taskEvent);
        }
      });
      sourceObservable = Observable
          .merge(source.takeUntil(subject), subject);*/
      //.take(1);

      sourceObservable
          .compose(takeNextAndUnsubscribe())
          .subscribe(new Subscriber<TaskEvent>() {
            @Override public void onCompleted() {
              System.out.println(" run onCompleted ");
            }

            @Override public void onError(Throwable e) {
              System.out.println(" run onError " + e);
            }

            @Override public void onNext(TaskEvent taskEvent) {
              System.out.println(" run onNext w " + taskEvent);
            }
          });
    }
  }

  static abstract class BaseTask {
    public PublishSubject<TaskEvent> obs = PublishSubject.create();
    String input;
    String output;

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
    @Override Observable<TaskEvent> run() {
      if (TextUtils.isEmpty(input)) {
        throw new IllegalArgumentException("I can't run without an input ");
      }
      System.out.println("Task2 running ");
      try {
        Thread.sleep(200);
      } catch (Exception e) {
        e.printStackTrace();
      }
      output = "Output task #2";
      System.out.println("Task2 completed");
      obs.onCompleted();
      return obs;
    }
  }

  static class Task3 extends BaseTask {
    @Override Observable<TaskEvent> run() {
      if (TextUtils.isEmpty(input)) {
        throw new IllegalArgumentException("I can't run without an input ");
      }
      System.out.println("Task3 running ");
      try {
        Thread.sleep(400);
      } catch (Exception e) {
          e.printStackTrace();
      }
      output = "Output task #3";
      System.out.println("Task3 completed");
      obs.onCompleted();
      return obs;
    }
  }
}
