create database invoice_db;

create user invoice_db_manager with password '123456';

grant connect on database invoice_db to invoice_db_manager;

\c invoice_db

grant create on schema public to invoice_db_manager;

alter default privileges in schema public
    grant select, insert, update, delete on tables to invoice_db_manager;

alter default privileges in schema public
    grant usage, select, update on sequences to invoice_db_manager;