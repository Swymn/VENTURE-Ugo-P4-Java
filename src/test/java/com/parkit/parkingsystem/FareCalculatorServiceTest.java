package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

class FareCalculatorServiceTest {


    private static FareCalculatorService fareCalculatorService;

    @Mock
    private static TicketDAO ticketDAO;

    private Ticket ticket;

    private static ParkingSpot parkingSpotCarNotAvailable;
    private static ParkingSpot parkingSpotBikeNotAvailable;
    private static ParkingSpot parkingSpotEmptyNotAvailable;

    @BeforeAll
    public static void setUp() {
        ticketDAO = mock(TicketDAO.class);
        fareCalculatorService = new FareCalculatorService(ticketDAO);
        parkingSpotCarNotAvailable = new ParkingSpot(1, ParkingType.CAR, false);
        parkingSpotBikeNotAvailable = new ParkingSpot(1, ParkingType.BIKE, false);
        parkingSpotEmptyNotAvailable = new ParkingSpot(1, null, false);
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    void calculateFareCar() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    void calculateFareBike() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();


        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotBikeNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    void calculateFareUnkownType() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotEmptyNotAvailable);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    void calculateFareBikeWithFutureInTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotBikeNotAvailable);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    void calculateFareBikeWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotBikeNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    void calculateFareCarWithLessThanOneHourParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    void calculateFareCarWithMoreThanADayParkingTime() {
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    void calculateFarCarWithLessThanThirtyMinParkingTime() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    void calculateFarCarWithThirtyMinParkingTime() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    void calculateFarBikeWithLessThanThirtyMinParkingTime() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (15 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    void calculateFarBikeWithThirtyMinParkingTime() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotBikeNotAvailable);
        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(0, ticket.getPrice());
    }

    @Test
    void calculateFareCarWithDiscount() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(ticket);

        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.CAR_RATE_PER_HOUR * 0.95, ticket.getPrice());
    }

    @Test
    void calculateFareCarWithoutDiscount() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotCarNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    void calculateFareBikeWithDiscount() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotBikeNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(ticket);

        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.BIKE_RATE_PER_HOUR * 0.95, ticket.getPrice());
    }

    @Test
    void calculateFareBikeWithoutDiscount() {
        final Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
        final Date outTime = new Date();

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpotBikeNotAvailable);

        when(ticketDAO.getTicket(ticket.getVehicleRegNumber())).thenReturn(null);

        fareCalculatorService.calculateFare(ticket, false);
        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }
}
