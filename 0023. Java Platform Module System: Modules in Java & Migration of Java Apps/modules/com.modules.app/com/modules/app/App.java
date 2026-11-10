package com.modules.app;

import java.util.ServiceLoader;
import com.modules.greetings.Greetings;
import com.modules.greetings.ServiceStatusLogger;

public class App {

    public static void main(String[] args) {
        Greetings.sayHello();

        // consuming Service example
        Iterable<ServiceStatusLogger> services = ServiceLoader.load(ServiceStatusLogger.class);
        ServiceStatusLogger service = services.iterator().next();
        service.printServiceStatusLog();
    }
}
