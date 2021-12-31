package com.journal.journalbackend.journal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface JournalRepository extends JpaRepository<Journal, Long> {
}
