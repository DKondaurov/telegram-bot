-- liquibase formatted sql

-- changeset dkondaurov:1

CREATE TABLE reminders
(
    id   INTEGER PRIMARY KEY,
    chat INTEGER,
    text TEXT,
    time TIMESTAMP
)