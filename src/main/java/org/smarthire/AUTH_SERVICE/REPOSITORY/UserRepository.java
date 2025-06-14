package org.smarthire.AUTH_SERVICE.REPOSITORY;

import org.smarthire.AUTH_SERVICE.MODELS.User;
import org.springframework.data.domain.Page; // For pagination
import org.springframework.data.domain.Pageable; // For pagination
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // For update/delete queries
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // For @Modifying queries

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // --- Existing methods (good to go!) ---
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<User> findByEmailIgnoreCaseAndEnableTrue(String email);
    Optional<User> findByPhoneNumberAndEnableTrue(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName AND u.enable = true")
    List<User> findByRoleNameAndEnableTrue(@Param("roleName") String roleName);

    List<User> findByEnableTrue();
    List<User> findByEnableFalse();

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.phoneNumber = :identifier")
    Optional<User> findByEmailOrPhoneNumber(@Param("identifier") String identifier);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain%")
    List<User> findByEmailDomain(@Param("domain") String domain);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.roleName = :roleName")
    Long countByRoleName(@Param("roleName") String roleName);

    Long countByEnableTrue();
    Long countByEnableFalse();

    // --- New / Recommended Additions ---

    /**
     * Finds a user by ID only if their account is enabled.
     * Useful for retrieving active users by their primary key.
     * @param id The ID of the user.
     * @return An Optional containing the enabled User, or empty if not found or not enabled.
     */
    Optional<User> findByIdAndEnableTrue(Long id);

    /**
     * Finds all users, ordered by email, with pagination.
     * @param pageable Pagination and sorting information.
     * @return A Page of User entities.
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Finds users by role name with pagination and sorting.
     * @param roleName The name of the role (e.g., "ROLE_USER", "ROLE_ADMIN").
     * @param pageable Pagination and sorting information.
     * @return A Page of User entities for the given role.
     */
    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    /**
     * Finds enabled users by role name with pagination and sorting.
     * @param roleName The name of the role.
     * @param pageable Pagination and sorting information.
     * @return A Page of enabled User entities for the given role.
     */
    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName AND u.enable = true")
    Page<User> findByRoleNameAndEnableTrue(@Param("roleName") String roleName, Pageable pageable);


    /**
     * Logically deletes a user by setting their 'enable' status to false.
     * This is a soft delete, preserving the user's data for auditing or potential reactivation.
     * Requires @Modifying and @Transactional.
     * @param userId The ID of the user to disable.
     * @return The number of entities updated (should be 1 if successful).
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.enable = false WHERE u.id = :userId")
    int softDeleteUser(@Param("userId") Long userId);

    /**
     * Reactivates a user's account by setting their 'enable' status to true.
     * Requires @Modifying and @Transactional.
     * @param userId The ID of the user to enable.
     * @return The number of entities updated (should be 1 if successful).
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.enable = true WHERE u.id = :userId")
    int enableUser(@Param("userId") Long userId);

    /**
     * Updates a user's password.
     * Requires @Modifying and @Transactional.
     * @param userId The ID of the user whose password to update.
     * @param newPassword The new encoded password.
     * @return The number of entities updated.
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.id = :userId")
    int updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);
}