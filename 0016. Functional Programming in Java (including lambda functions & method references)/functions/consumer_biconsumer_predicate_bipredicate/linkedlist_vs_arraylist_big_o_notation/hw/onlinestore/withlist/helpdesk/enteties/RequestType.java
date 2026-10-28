package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.enteties;

import static functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.enteties.Priority.HIGH;
import static functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.enteties.Priority.LOW;
import static functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.enteties.Priority.MEDIUM;

public enum RequestType {
    OTHER(LOW), CHANGE_ACCOUNT_DETAILS(LOW), CAN_NOT_LOGIN(MEDIUM),
    ACCOUNT_IS_BLOCKED(MEDIUM), COOPERATION(MEDIUM), ACCOUNT_IS_HACKED(HIGH),
    CAN_NOT_COMPLETE_PURCHASE(HIGH), ORDER_IS_NOT_RECEIVED(HIGH);

    private Priority priority;

    private RequestType(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return this.priority;
    }
}
