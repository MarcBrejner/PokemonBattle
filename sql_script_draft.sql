#CREATE database pokemonGame DEFAULT character set utf8mb4;

# for setting up, comment the DROP requests and uncomment the CREATE one
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
level int DEFAULT 1,
xp int DEFAULT 0,
requiredXp int AS (level*4),
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
level int DEFAULT 1,
xp int DEFAULT 0,
requiredXp int AS (level*2),
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