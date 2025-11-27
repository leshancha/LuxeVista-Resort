package com.example.luxres;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update; // Import Update annotation
import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUser(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findByEmail(String email);

    @Query("SELECT * FROM users WHERE uid = :userId LIMIT 1")
    UserEntity findById(int userId);

    @Query("SELECT * FROM users")
    List<UserEntity> getAllUsers();

    /**
     * Updates an existing user in the database.
     * Room uses the primary key (uid) of the passed UserEntity to find the row to update.
     *
     * @param user The UserEntity object with updated information.
     * @return The number of rows affected (should be 1 if successful).
     */
    @Update
    int updateUser(UserEntity user); // Add this update method

    // Add other methods as needed (e.g., deleteUser)
}
