package demo.flightcontrols;

import com.cloudrti.client.api.flightcontrols.FlightControl;
import rx.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ExampleFlightControl implements FlightControl {
    @Override
    public String getName() {
        return "DemoFlightControl";
    }

    @Override
    public Observable<String> execute(Map<String, String> data) {
        System.out.println("example flight control: " + data);

        return Observable.create(observer -> {
            for(int i = 0; i < 5; i++) {
                observer.onNext("message " + i);
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            observer.onCompleted();
        });
    }

    @Override
    public List<String> getArgumentNames() {
        return Arrays.asList("a", "b", "c");
    }
}
