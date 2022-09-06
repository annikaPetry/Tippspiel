CREATE TABLE Benutzer(
	Benutzername varchar(255) NOT NULL,
	Passwort varchar(255) NOT NULL,
	PRIMARY KEY(Benutzername)
);

CREATE TABLE Manager(
	Benutzername varchar(255) NOT NULL,
	Passwort varchar(255) NOT NULL,
	PRIMARY KEY(Benutzername)
);

CREATE TABLE Mannschaften(
	MannschaftsID INT NOT NULL GENERATED ALWAYS AS IDENTITY(
        START WITH 1, INCREMENT BY 1),
	Mannschaftsname varchar(255) NOT NULL,
	PRIMARY KEY(MannschaftsID)
);

CREATE TABLE Turniere(
	TurnierID INT NOT NULL GENERATED ALWAYS AS IDENTITY(
        START WITH 1, INCREMENT BY 1),
	Turniername varchar(255) NOT NULL,
	PRIMARY KEY(TurnierID)
);

CREATE TABLE Spiele(
	SpielID INT NOT NULL GENERATED ALWAYS AS IDENTITY(
        START WITH 1, INCREMENT BY 1),
	Team1 INT NOT NULL,
	Team2 INT NOT NULL,
	ToreT1 int DEFAULT 0,
	ToreT2 int DEFAULT 0,
	Anstossdatum Date NOT NULL,
	Anstosszeit Time NOT NULL,
	Turnier INT NOT NULL,
	Status_Offen boolean NOT NULL DEFAULT true,
	Status_Ergebnis boolean NOT NULL DEFAULT false,
	PRIMARY KEY (SpielID),
		FOREIGN KEY (Turnier) REFERENCES Turniere(TurnierID),
        FOREIGN KEY (Team1) REFERENCES Mannschaften(MannschaftsID),
        FOREIGN KEY (Team2) REFERENCES Mannschaften(MannschaftsID)
);


CREATE TABLE Tipps(
	SpielID INT NOT NULL, 
	Benutzer varchar(255) NOT NULL,
	TippTore1 int NOT NULL,
	TippTore2 int NOT NULL,
	PRIMARY KEY (SpielID,Benutzer),
        FOREIGN KEY (SpielID) REFERENCES Spiele(SpielID),
        FOREIGN KEY (Benutzer) REFERENCES Benutzer(Benutzername)
);

INSERT INTO Manager VALUES ('Admin', 'Admin');
INSERT INTO Manager VALUES ('Admin1', 'Admin1');