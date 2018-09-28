create table company(
	info varchar(20),
    description varchar(20),
	legal_person varchar(10),
	imgUrl blob,
	address varchar(50),
	contact_tel varchar(20),
	contact_email varchar(30)
);

create table community(
	name varchar(20) not null,
	address varchar(50),
	floor_area number(5,2),
	total_area number(5,2),
	green_area number(5,2),
	crttime timestamp,
	description clob,
    primary key(name)
);

create table building(
	building_id varchar(3),
	community_name varchar(20),
	building_type varchar(10),
	floor_area number(5,2),
	floor_num number(3) check (floor_num>0),
	direction varchar(10),
	height number(4,2),
	crttime timestamp,
	description varchar(100),
	primary key(building_id,community_name),
	foreign key (community_name) references community(name)
);

create table room(
	building_id varchar(3),
	community_name varchar(20),
	room_id varchar(4),
	floor_id number(3) check (floor_id>0),
	room_layout varchar(8),
	room_area number(5,2),
	room_type number(5,2),
	room_use clob,
	is_vacancy number(1) check (is_vacancy in (0,1)),
	decorated number(1) check (decorated in (0,1)),
	primary key(community_name,building_id,floor_id,room_id),
	foreign key (community_name,building_id) references building(community_name,building_id)
);

create table department(
   did varchar(10) unique,
   community_name varchar(20),
   name varchar(10),
   manager varchar(10),
   primary key(community_name,name),
   foreign key (community_name) references community(name)
);

create table contract(
	contract_id char(10),
    crttime timestamp,
    deadtime timestamp,
    valid number(1) check (valid in (0,1)),
    img blob,
    creator varchar(10),
    primary key (contract_id)   
);

create table employee(
   eid varchar(10),
   name varchar(10),
   sex varchar(6),
   birthday date,
   tel varchar(11),
   did varchar(10),
   decription varchar(50),
   contract_id varchar(10),
   entry_time timestamp,
   primary key(eid),
   foreign key (did) references department(did)
);






create table owner(
	building_id varchar(3),
	community_name varchar(20),
	room_id varchar(4),
	owner_id varchar(18) unique,
	floor_id number(3) check (floor_id>0),
	contract_id char(10),
	name varchar(20) not null,
	sex varchar(5),
	age number(3) check (age>0),
	tel varchar(11),
	hukou varchar(50),
	work_place varchar(50),
	contract_address varchar(50),
	postalcode varchar(6),
	check_in_time long,
	pay_way varchar(13),
	remark clob,
	primary key (owner_id,community_name,building_id,room_id),
	foreign key (community_name,building_id,floor_id,room_id) references room(community_name,building_id,floor_id,room_id),
	foreign key (contract_id) references contract(contract_id)
);

create table owner_family(
	building_id varchar(3),
	community_name varchar(20),
	room_id varchar(4),
	owner_id varchar(18),
	floor_id number(3) check (floor_id>0),
	member_id varchar(18),
	sex varchar(5),
	relation varchar(4),
	hukou varchar(50),
	work_study_place varchar(50),
	tel varchar(11),
	primary key (owner_id,member_id),
	foreign key (owner_id,community_name,building_id,room_id) references owner(owner_id,community_name,building_id,room_id)
);

create table car(
	building_id varchar(3),
	community_name varchar(20),
	room_id varchar(4),
	owner_id varchar(18),
	label varchar(15),
	color varchar(8),
	car_id varchar(10),
	primary key(owner_id,car_id),
	foreign key (owner_id,community_name,building_id,room_id) references owner(owner_id,community_name,building_id,room_id)
);

create table pet(
	building_id varchar(3),
	community_name varchar(20),
	room_id varchar(4),
	owner_id varchar(18),
	variety varchar(15),
	tall number(2,2),
	pet_id varchar(10),
	primary key(owner_id,pet_id),
	foreign key (owner_id,community_name,building_id,room_id) references owner(owner_id,community_name,building_id,room_id)
);

create table device(
	device_id varchar(15) primary key not null,
	name varchar(20) not  null,
	version varchar(20) not null,
	floor_id varchar(3) not null,
	building_id varchar(3) not null,
	community_name varchar(20) not null,
	price number(5,2) not null check (price>0),
	install_time timestamp,
	bad_time timestamp,
	install_man varchar(8),
	status varchar(6),
	foreign key (install_man) references employee(eid),
	foreign key (community_name,building_id) references building(community_name,building_id)
);

create table charge_item(
	item_id varchar(10) not null primary key,
	item_type varchar(10) not null,
	descript clob,
	fee number(5,2)
);

create table charge_form(
	form_id varchar(18) not null primary key,
	building_id varchar(3),
	community_name varchar(20),
	room_id varchar(4),
	floor_id number(3) check (floor_id>0),
	owner_id varchar(18),
	pay_way varchar(10),
	item_id varchar(10),
	item_num number(3),
	start_time timestamp,
	end_time timestamp,
	receivable number(5,2),
	real_receipt number(5,2),
	balance number(5,2),
	form_creater varchar(10),
	form_crttime timestamp,
	foreign key (community_name,building_id,floor_id,room_id) references room(community_name,building_id,floor_id,room_id),
	foreign key (item_id) references charge_item(item_id)
);

create table repair_report(
	report_id varchar(10) not null primary key,
	owner_id varchar(18) check (length(owner_id)=18 and length(owner_id)=15),
	receiver varchar(10) not null,
	contract_tel varchar(11),
	report_time timestamp,
	distribute_time timestamp,
	descript clob,
	worker varchar(10),
	worket_tel varchar(11),
	finished_time timestamp
);

create table device_check(
	device_id varchar(15) not null,
	check_frequency varchar(10),
	descript clob,
	result clob,
	checker varchar(10)
);

	create table users(
		id  varchar(10) not null,
		rel_id varchar(18),
		pwd varchar(50),
		primary key(id)
	);
	create table roles(
		id varchar(10) not null,
		name varchar(10) not null,
		description varchar(50),
		available integer,
		primary key(id)
	);
	create table users_roles(
		user_id varchar(10),
		role_id varchar(10),
		primary key(user_id),
		foreign key (user_id) references users(id),
		foreign key (role_id) references roles(id)
	);
	create table resources(
		id varchar(10) not null,
		name varchar(10) not null,
		fid varchar(10),
		url varchar(20),
		primary key(id)
	);

	create table roles_resources(
		role_id varchar(10) not null,
		resource_id varchar(10) not null,
		primary key (role_id,resource_id),
		foreign key (role_id) references roles(id),
		foreign key (resource_id) references resources(id)
	);

