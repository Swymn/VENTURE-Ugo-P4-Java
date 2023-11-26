package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    @Test
    void testParkingACar() {
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception ignored) {

        }

        int baseTicketAmount = ticketDAO.getNbTicket("ABCDEF");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        int newTicketAmount = ticketDAO.getNbTicket("ABCDEF");
        Assertions.assertTrue(newTicketAmount > baseTicketAmount);
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    }

    @Test
    void testParkingLotExit() {
        try {
            testParkingACar();
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        } catch (Exception ignored) {

        }
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        int spot = parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        Ticket newTIcket = ticketDAO.getTicket("ABCDEF");
        int newSpot = parkingSpotDAO.getNextAvailableSlot(ticket.getParkingSpot().getParkingType());

        Assertions.assertNotEquals(ticket.getOutTime(), newTIcket.getOutTime());

        Assertions.assertNotEquals(spot, newSpot);
        //TODO: check that the fare generated and out time are populated correctly in the database
    }

}
