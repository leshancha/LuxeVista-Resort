package com.example.luxres; // Use your correct package name

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for the BookingEntity.
 */
@Dao
public interface BookingDao {

    /**
     * Inserts a new booking. Replaces if conflict (based on primary key booking_id).
     * Consider ABORT strategy if booking IDs must be absolutely unique on first insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBooking(BookingEntity booking);

    /**
     * Gets all bookings for a specific user, ordered by start date descending.
     * @param userId The ID of the user whose bookings to retrieve.
     * @return A list of BookingEntity objects.
     */
    @Query("SELECT * FROM bookings WHERE user_id = :userId ORDER BY start_date DESC")
    List<BookingEntity> getBookingsForUser(int userId);

    /**
     * Deletes a booking by its ID.
     * @param bookingId The ID of the booking to delete.
     * @return The number of rows affected (should be 1 if successful).
     */
    @Query("DELETE FROM bookings WHERE booking_id = :bookingId")
    int deleteBookingById(String bookingId);

    /**
     * Updates the status of a specific booking.
     * (Alternative to deleting for cancellation)
     * @param bookingId The ID of the booking to update.
     * @param newStatus The new status string (e.g., "Cancelled").
     * @return The number of rows affected.
     */
    @Query("UPDATE bookings SET status = :newStatus WHERE booking_id = :bookingId")
    int updateBookingStatus(String bookingId, String newStatus);

    /**
     * Updates specific fields of a booking (e.g., dates).
     * Requires a full BookingEntity object.
     */
    @Update
    int updateBooking(BookingEntity booking); // For changing dates etc.

    // Add other queries as needed (e.g., find booking by ID)
    @Query("SELECT * FROM bookings WHERE booking_id = :bookingId LIMIT 1")
    BookingEntity findBookingById(String bookingId);

}
