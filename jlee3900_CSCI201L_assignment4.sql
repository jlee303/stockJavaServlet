
CREATE DATABASE Assignment_4_Database;
CREATE TABLE Assignment_4_Database.Users(UID int NOT NULL AUTO_INCREMENT PRIMARY KEY, Username varchar(255) UNIQUE,  Email varchar(255) UNIQUE, Password varchar(255),  Balance float);
CREATE TABLE Assignment_4_Database.Favorites(FID int NOT NULL AUTO_INCREMENT PRIMARY KEY, UID int, FOREIGN KEY(UID) REFERENCES Users(UID), Ticker varchar(255), TickerName varchar(255));
CREATE TABLE Assignment_4_Database.Portfolio(PID int NOT NULL AUTO_INCREMENT PRIMARY KEY, UID int, FOREIGN KEY(UID) REFERENCES Users(UID), Ticker varchar(255), Quantity int, TickerName varchar(255), TotalCost float);
