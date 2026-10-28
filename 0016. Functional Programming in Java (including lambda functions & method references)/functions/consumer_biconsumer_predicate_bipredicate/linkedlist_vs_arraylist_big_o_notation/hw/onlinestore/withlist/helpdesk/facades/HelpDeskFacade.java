package functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.facades;

import functions.consumer_biconsumer_predicate_bipredicate.linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.enteties.SupportTicket;

public interface HelpDeskFacade {

    void addNewSupportTicket(SupportTicket supportTicket);

    SupportTicket getNextSupportTicket();

    /**
     * @return amount of tickets that are not processed
     */
    int getNumberOfTickets();
}
