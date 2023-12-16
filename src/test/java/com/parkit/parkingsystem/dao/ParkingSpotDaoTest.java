package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParkingSpotDaoTest {

    private ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        // You might need to set up a test database or use a mocking framework for database interactions.
    }

    @Test
    void testGetNextAvailableSlot() {
        int result = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        // Assert the result based on your test scenario
        Assertions.assertEquals(2, result);
    }

    @Test
    void testUpdateParking() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
        parkingSpot.setId(1);
        parkingSpot.setAvailable(false);

        boolean updated = parkingSpotDAO.updateParking(parkingSpot);
        Assertions.assertTrue(updated);
    }

    // Add more test cases as needed

    @AfterEach
    void tearDown() {
        // Clean up resources if necessary
    }
}
