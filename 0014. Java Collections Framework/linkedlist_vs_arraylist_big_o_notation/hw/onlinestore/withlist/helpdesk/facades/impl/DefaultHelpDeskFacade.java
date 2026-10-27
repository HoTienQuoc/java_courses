package linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.facades.impl;

import java.util.PriorityQueue;
import java.util.Queue;

import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.enteties.SupportTicket;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.facades.HelpDeskFacade;
import linkedlist_vs_arraylist_big_o_notation.hw.onlinestore.withlist.helpdesk.utils.CustomSupportTicketsComparator;

public class DefaultHelpDeskFacade implements HelpDeskFacade {

    private Queue<SupportTicket> tickets;

    {
        tickets = new PriorityQueue<>(new CustomSupportTicketsComparator());
    }

    @Override
    public void addNewSupportTicket(SupportTicket supportTicket) {
        tickets.offer(supportTicket);
    }

    @Override
    public SupportTicket getNextSupportTicket() {
        return tickets.poll();
    }

    @Override
    public int getNumberOfTickets() {
        return tickets.size();
    }

}
