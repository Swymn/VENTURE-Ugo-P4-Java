package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParkingSpotTest {

    @Test
    void updateParkingType() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setParkingType(ParkingType.BIKE);

        Assertions.assertEquals(ParkingType.BIKE, parkingSpot.getParkingType());
    }

    @Test
    void updateId() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setId(2);

        Assertions.assertEquals(2, parkingSpot.getId());
    }

    @Test
    void updateAvailable() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        parkingSpot.setAvailable(false);

        Assertions.assertFalse(parkingSpot.isAvailable());
    }

    @Test
    void equals() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.CAR, true);

        Assertions.assertEquals(parkingSpot, parkingSpot2);
    }

    @Test
    void testHashCode() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);
        ParkingSpot parkingSpot2 = new ParkingSpot(1, ParkingType.CAR, true);

        Assertions.assertEquals(parkingSpot.hashCode(), parkingSpot2.hashCode());
    }
}
