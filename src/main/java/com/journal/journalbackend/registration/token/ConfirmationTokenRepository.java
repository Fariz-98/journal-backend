package com.journal.journalbackend.registration.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    ConfirmationToken findByToken(String token);

    @Query("FROM ConfirmationToken as ct where ct.appUser.id = :appUserId")
    ConfirmationToken findByAppUserId(@Param("appUserId") Long id);

}
