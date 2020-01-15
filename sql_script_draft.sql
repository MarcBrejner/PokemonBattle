#CREATE database pokemonGame DEFAULT character set utf8mb4;

DROP TABLE pokemonGame.Ability;
DROP TABLE pokemonGame.Pokemon;
DROP TABLE pokemonGame.Item;
DROP TABLE pokemonGame.Profile;
DROP TABLE pokemonGame.Authentication;

CREATE TABLE pokemonGame.Authentication (
id INT NOT NULL auto_increment,
username varchar(20) NOT NULL,
password varchar(20) NOT NULL,
PRIMARY KEY(id),
UNIQUE ( username )
);

CREATE TABLE pokemonGame.Profile (
id INT NOT NULL auto_increment,
username varchar(20) NOT NULL,
level int,
PRIMARY KEY ( id ),
FOREIGN KEY (username) REFERENCES Authentication(username),
UNIQUE ( username )
);

CREATE TABLE pokemonGame.Pokemon (
id INT NOT NULL auto_increment,
masterId int,
name varchar(30),
element varchar(20),
status varchar(20),
hp int,
maxHp int,
alive boolean,
PRIMARY KEY (id),
FOREIGN KEY (masterId) REFERENCES Profile(id)
);

CREATE TABLE pokemonGame.Ability (
id INT NOT NULL auto_increment,
pokemonId int,
name varchar(20),
element varchar(20),
type varchar(20),
enemyEffect boolean,
selfEffect boolean,
cost int,
value int,
selfValue int,
enemyStatus varchar(20),
selfStatus varchar(20),
PRIMARY KEY(id),
FOREIGN KEY(pokemonId) REFERENCES Pokemon(id)
);

CREATE TABLE pokemonGame.Item (
id INT NOT NULL auto_increment,
ownerId int,
name varchar(20),
type varchar(20),
value int,
number int,
PRIMARY KEY(id),
FOREIGN KEY(ownerId) REFERENCES Profile(id)
);

INSERT INTO pokemonGame.Authentication(username, password) VALUES ("vincent", "password");
INSERT INTO pokemonGame.Authentication(username, password) VALUES ("franck", "password");

INSERT INTO pokemonGame.Profile(username, level) VALUES ("vincent", 0);
INSERT INTO pokemonGame.Profile(username, level) VALUES ("franck", 0);

INSERT INTO pokemonGame.Pokemon(masterId, name, element, status, hp, maxHp, alive)
VALUES (1, "Pikachu", "Electric", "None", 80, 80, true);

INSERT INTO pokemonGame.Ability(pokemonId, name, element, type, enemyEffect, selfEffect, cost, value, selfValue, enemyStatus, selfStatus)
VALUES (1, "Bolt", "Electric", "Damage", true, false, 2, -30, 0, "Paralyzed", "None");
INSERT INTO pokemonGame.Ability(pokemonId, name, element, type, enemyEffect, selfEffect, cost, value, selfValue, enemyStatus, selfStatus)
VALUES (1, "Slap", "Normal", "Damage",true, false, 2, -30, 0, "None", "None");

INSERT INTO pokemonGame.Item(ownerId, name, type, value, number) VALUES (1, "Potion", "Heal", 25, 2);

SELECT * FROM pokemonGame.Profile;
SELECT * FROM pokemonGame.Item WHERE id=1;
SELECT * FROM pokemonGame.Pokemon WHERE masterId=1;
SELECT * FROM pokemonGame.Ability WHERE pokemonId=1;
SELECT * FROM pokemonGame.Authentication;

#UPDATE pokemonGame.Authentication SET password = "pwd" WHERE username = "franck";
#SELECT * FROM pokemonGame.Authentication WHERE username = "franck";

#DELETE from pokemonGame.Profile WHERE id = 2;