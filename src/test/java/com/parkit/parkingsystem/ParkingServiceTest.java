package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    void setUpPerTest() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");

            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    void processExitingVehicleTest() {
        when(ticketDAO.getTicket(anyString())).thenReturn(createFakeTicket());
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processExitingVehicle();

        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    void testProcessIncomingVehicle() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(2);

        parkingService.processIncomingVehicle();
        
        verify(parkingSpotDAO, times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    /**
     * Same as the previous test,
     * this one is not working because of PotentialStubbingProblem because of the mock inside this test and the setup.
     *
     * @FIXME
     */
    @Test
    void processExitingVehicleTestUnableUpdate() {
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(false);
        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));
    }

    /**
     * This one isn't working either because of the inputReaderUtil.readSelection() method which given a wrong value (0).
     *
     * @FIXME
     */
    @Test
    void testGetNextParkingNumberIfAvailable() {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
        parkingService.getNextParkingNumberIfAvailable();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    /**
     * This one isn't working either because of the inputReaderUtil.readSelection() method which given a wrong value (0).
     *
     * @FIXME
     */
    @Test
    void testGetNextParkingNumberIfAvailableParkingNotFound() {
        when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(-1);
        parkingService.getNextParkingNumberIfAvailable();
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
    }

    @Test
    void testGetNextParkingNumberIfAvailableParkingNumberWrongArgument() {
        // TBD
    }

    private Ticket createFakeTicket() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");
        return ticket;
    }
}