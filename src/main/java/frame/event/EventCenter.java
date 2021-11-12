package frame.event;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.function.Consumer;

public class EventCenter {
    private static final Map<Class<? extends EventObject>, SubmissionPublisher<EventObject>> eventTable = new HashMap<>();

    public static void subscribe(Class<? extends EventObject> eventClass, Flow.Subscriber<EventObject> subscriber) {
        if (!eventTable.containsKey(eventClass)) eventTable.put(eventClass, new SubmissionPublisher<>());
        eventTable.get(eventClass).subscribe(subscriber);
    }

    public static void subscribe(Class<? extends EventObject> eventClass, Consumer<EventObject> onNext) {
        subscribe(eventClass, new Flow.Subscriber<EventObject>() {
            Flow.Subscription subscription;
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(EventObject item) {
                onNext.accept(item);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    public static void publish(EventObject event) {
        eventTable.get(event.getClass()).submit(event);
    }
}
