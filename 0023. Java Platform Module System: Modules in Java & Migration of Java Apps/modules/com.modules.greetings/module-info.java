module com.modules.greetings {
    exports com.modules.greetings;
    provides com.modules.greetings.ServiceStatusLogger with com.modules.greetings.DefaultServiceStatusLogger;
}
