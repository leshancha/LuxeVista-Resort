package com.example.luxres;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

// Placeholder for API endpoint definitions (using Retrofit syntax)
public interface ApiService {

    // --- Authentication ---
    @POST("auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest); // Define AuthResponse and LoginRequest models

    @POST("auth/register")
    Call<ApiResponse> registerUser(@Body RegisterRequest registerRequest); // Define ApiResponse and RegisterRequest models

    // --- Rooms ---
    @GET("rooms")
    Call<List<Room>> getRooms(@Query("available") Boolean available, @Query("type") String roomType); // Example filters

    @GET("rooms/{roomId}")
    Call<Room> getRoomDetails(@Path("roomId") String roomId);

    // --- Services ---
    @GET("services")
    Call<List<Service>> getServices(@Query("category") String category);

    @GET("services/{serviceId}")
    Call<Service> getServiceDetails(@Path("serviceId") String serviceId);

    // --- Bookings ---
    @POST("bookings")
    Call<Booking> createBooking(@Header("Authorization") String token, @Body BookingRequest bookingRequest); // Define BookingRequest

    @GET("bookings/my")
    Call<List<Booking>> getMyBookings(@Header("Authorization") String token);

    @POST("bookings/{bookingId}/cancel")
    Call<ApiResponse> cancelBooking(@Header("Authorization") String token, @Path("bookingId") String bookingId);

    // --- Attractions/Offers ---
    @GET("attractions")
    Call<List<Attraction>> getAttractions();

    // --- User Profile ---
    @GET("profile")
    Call<User> getUserProfile(@Header("Authorization") String token);

    // Define request/response models (AuthResponse, LoginRequest, ApiResponse, etc.) as needed
    // Example placeholder models:
    class LoginRequest { String email; String password; }
    class RegisterRequest { String name; String email; String password; }
    class AuthResponse { String token; User user; } // Example response with token and user data
    class ApiResponse { boolean success; String message; } // Generic API response
    class BookingRequest { String itemId; String itemType; String startDate; String endDate; /* ... other details */ }

}