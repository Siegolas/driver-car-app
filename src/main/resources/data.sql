/**
 * CREATE Script for init of DB
 */

-- Create manufacturers

insert into manufacturer (id, date_created, name) values
(1, now(), 'ALFA ROMEO');

insert into manufacturer (id, date_created, name) values
(2, now(), 'TOYOTA');


-- Create cars

insert into car (id, date_created, license_plate, seat_count, convertible, rating, engine_type, deleted,
manufacturer_id) values
(1, now(), 'B9863US', 5, false, 70, 'GASOLINE', false, 1);

insert into car (id, date_created, license_plate, seat_count, convertible, rating, engine_type, deleted, manufacturer_id) values
(2, now(), '6392KPT', 2, true, 60, 'HYBRID', false, 2);


-- Create 3 OFFLINE drivers

insert into driver (id, date_created, deleted, online_status, password, username) values (1, now(), false, 'OFFLINE',
'driver01pw', 'driver01');

insert into driver (id, date_created, deleted, online_status, password, username) values (2, now(), false, 'OFFLINE',
'driver02pw', 'driver02');

insert into driver (id, date_created, deleted, online_status, password, username) values (3, now(), false, 'OFFLINE',
'driver03pw', 'driver03');


-- Create 3 ONLINE drivers

insert into driver (id, date_created, deleted, online_status, password, username, car_id) values (4, now(), false,
'ONLINE',
'driver04pw', 'driver04', 1);

insert into driver (id, date_created, deleted, online_status, password, username, car_id) values (5, now(), false, 'ONLINE',
'driver05pw', 'driver05', 2);

insert into driver (id, date_created, deleted, online_status, password, username) values (6, now(), false, 'ONLINE',
'driver06pw', 'driver06');

-- Create 1 OFFLINE driver with coordinate(longitude=9.5&latitude=55.954)

insert into driver (id, coordinate, date_coordinate_updated, date_created, deleted, online_status, password, username)
values
 (7,
 'aced0005737200226f72672e737072696e676672616d65776f726b2e646174612e67656f2e506f696e7431b9e90ef11a4006020002440001784400017978704023000000000000404bfa1cac083127', now(), now(), false, 'OFFLINE',
'driver07pw', 'driver07');

-- Create 1 ONLINE driver with coordinate(longitude=9.5&latitude=55.954)

insert into driver (id, coordinate, date_coordinate_updated, date_created, deleted, online_status, password, username)
values
 (8,
 'aced0005737200226f72672e737072696e676672616d65776f726b2e646174612e67656f2e506f696e7431b9e90ef11a4006020002440001784400017978704023000000000000404bfa1cac083127', now(), now(), false, 'ONLINE',
'driver08pw', 'driver08');