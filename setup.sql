CREATE DATABASE journal_db;
CREATE USER 'journaluser'@'%' IDENTIFIED BY 'journalpassword';
GRANT ALL ON journal_db.* TO 'journaluser'@'%';
GRANT ALL ON journal_db.* TO 'journaluser'@'localhost';
GRANT ALL ON journal_db.* TO 'journaluser'@'journal_db_container';
GRANT ALL ON journal_db.* TO 'journaluser'@'journal_db';