package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private final TicketDAO dao;

    public FareCalculatorService(TicketDAO ticketDAO) {
        this.dao = ticketDAO;
    }

    public void calculateFare(final Ticket ticket) {
        calculateFare(ticket, true);
    }

    public void calculateFare(final Ticket ticket, final boolean discount) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        long durationMillis = outTimeMillis - inTimeMillis;

        double durationHours = (double) durationMillis / (1000 * 60 * 60);

        if (durationHours <= 0.5) {
            ticket.setPrice(0);
            return;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice((durationHours * Fare.CAR_RATE_PER_HOUR) * getReductionFare(discount));
                break;
            }
            case BIKE: {
                ticket.setPrice((durationHours * Fare.BIKE_RATE_PER_HOUR) * getReductionFare(discount));
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }

    private double getReductionFare(final boolean discount) {
        return discount ? 0.95 : 1;
    }
}