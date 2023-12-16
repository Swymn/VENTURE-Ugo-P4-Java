package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;

class TicketDAOTest {

    private TicketDAO ticketDAO;
    private Connection connectionMock;
    private DataBaseConfig dataBaseConfigMock;

    @BeforeEach
    void setUp() throws SQLException, ClassNotFoundException {
        ticketDAO = new TicketDAO();
        // Mocking the dataBaseConfig.getConnection() method
        dataBaseConfigMock = Mockito.mock(DataBaseConfig.class);
        connectionMock = Mockito.mock(Connection.class);
        Mockito.when(dataBaseConfigMock.getConnection()).thenReturn(connectionMock);
    }

    @Test
    void testSaveTicket() throws SQLException {
        // Mocking the PreparedStatement and execute method
        PreparedStatement preparedStatementMock = Mockito.mock(PreparedStatement.class);
        Mockito.when(connectionMock.prepareStatement(DBConstants.SAVE_TICKET)).thenReturn(preparedStatementMock);
        Mockito.when(preparedStatementMock.execute()).thenReturn(true);

        // Setting up a test ticket
        Ticket ticket = new Ticket();
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber("ABC123");
        ticket.setPrice(5.0);
        ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));

        // Assigning the mock to the DAO
        ticketDAO.dataBaseConfig = dataBaseConfigMock;

        // Performing the test
        boolean result = ticketDAO.saveTicket(ticket);

        // Verifying that the execute method was called
        Mockito.verify(preparedStatementMock).execute();

        // Asserting the result
        Assertions.assertTrue(result);
    }

    @Test
    void testGetTicket() throws SQLException {
        // Mocking the PreparedStatement, executeQuery method, and ResultSet
        PreparedStatement preparedStatementMock = Mockito.mock(PreparedStatement.class);
        Mockito.when(connectionMock.prepareStatement(DBConstants.GET_TICKET)).thenReturn(preparedStatementMock);

        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        // Setting up a test result in the ResultSet
        Mockito.when(resultSetMock.next()).thenReturn(true);
        Mockito.when(resultSetMock.getInt(1)).thenReturn(1);
        Mockito.when(resultSetMock.getInt(2)).thenReturn(2);
        Mockito.when(resultSetMock.getString(6)).thenReturn(ParkingType.CAR.toString());

        // Setting up a test ticket
        String vehicleRegNumber = "ABC123";

        // Assigning the mock to the DAO
        ticketDAO.dataBaseConfig = dataBaseConfigMock;

        // Performing the test
        Ticket result = ticketDAO.getTicket(vehicleRegNumber);

        // Verifying that the methods were called
        Mockito.verify(preparedStatementMock).setString(1, vehicleRegNumber);
        Mockito.verify(resultSetMock).next();

        // Asserting the result
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getParkingSpot().getId());
        Assertions.assertEquals(2, result.getId());
        Assertions.assertEquals(vehicleRegNumber, result.getVehicleRegNumber());
    }

    @Test
    void testUpdateTicket() throws SQLException {
        // Mocking the PreparedStatement and execute method
        PreparedStatement preparedStatementMock = Mockito.mock(PreparedStatement.class);
        Mockito.when(connectionMock.prepareStatement(DBConstants.UPDATE_TICKET)).thenReturn(preparedStatementMock);
        Mockito.when(preparedStatementMock.execute()).thenReturn(true);

        // Setting up a test ticket
        Ticket ticket = new Ticket();
        ticket.setPrice(10.0);
        ticket.setOutTime(new java.util.Date());
        ticket.setId(1);

        // Assigning the mock to the DAO
        ticketDAO.dataBaseConfig = dataBaseConfigMock;

        // Performing the test
        boolean result = ticketDAO.updateTicket(ticket);

        // Verifying that the execute method was called
        Mockito.verify(preparedStatementMock).setDouble(1, ticket.getPrice());
        Mockito.verify(preparedStatementMock).setTimestamp(2, new java.sql.Timestamp(ticket.getOutTime().getTime()));
        Mockito.verify(preparedStatementMock).setInt(3, ticket.getId());
        Mockito.verify(preparedStatementMock).execute();

        // Asserting the result
        Assertions.assertTrue(result);
    }

    @Test
    void testGetNbTicket() throws SQLException {
        // Mocking the PreparedStatement, executeQuery method, and ResultSet
        PreparedStatement preparedStatementMock = Mockito.mock(PreparedStatement.class);
        Mockito.when(connectionMock.prepareStatement(DBConstants.GET_NB_TICKET)).thenReturn(preparedStatementMock);

        ResultSet resultSetMock = Mockito.mock(ResultSet.class);
        Mockito.when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        // Setting up a test result in the ResultSet
        Mockito.when(resultSetMock.next()).thenReturn(true);
        Mockito.when(resultSetMock.getInt(1)).thenReturn(3); // Example count

        // Setting up a test vehicle registration number
        String vehicleRegNumber = "ABC123";

        // Assigning the mock to the DAO
        ticketDAO.dataBaseConfig = dataBaseConfigMock;

        // Performing the test
        int result = ticketDAO.getNbTicket(vehicleRegNumber);

        // Verifying that the methods were called
        Mockito.verify(preparedStatementMock).setString(1, vehicleRegNumber);
        Mockito.verify(resultSetMock).next();
        Mockito.verify(resultSetMock).getInt(1);

        // Asserting the result
        Assertions.assertEquals(3, result);
    }

    @AfterEach
    void tearDown() {
        // Clean up resources if necessary
    }

}
