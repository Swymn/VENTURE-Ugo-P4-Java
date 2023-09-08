package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    private final TicketDAO dao;

    public FareCalculatorService() {
        this.dao = new TicketDAO();
    }

    public void calculateFare(Ticket ticket) {
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
                ticket.setPrice((durationHours * Fare.CAR_RATE_PER_HOUR) * getReductionFare(ticket));
                break;
            }
            case BIKE: {
                ticket.setPrice((durationHours * Fare.BIKE_RATE_PER_HOUR) * getReductionFare(ticket));
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }

    private double getReductionFare(final Ticket ticket) {
        if (dao.getTicket(ticket.getVehicleRegNumber()) != null) {
            return 0.95;
        }
        return 1;
    }
}