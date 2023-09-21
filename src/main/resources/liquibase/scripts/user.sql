-- liquibase formatted sql

-- changeset esimonaytene:1
CREATE TABLE notification_task(
id SERIAL PRIMARY KEY,
text TEXT not null,
chat_id SERIAL not null,
date_time TIMESTAMP not null
);