create database ManageUser;
use ManageUser;
create table users(
    id int primary key auto_increment,
    name varchar(100),
    email varchar(255) unique
);
create table accounts(
    id int primary key auto_increment,
    name varchar(100),
    balance decimal(15,2) check ( balance > 0 )
);
create table bank_accounts(
    account_id int primary key auto_increment,
    account_name varchar(100),
    balance decimal(15,2),
    bank_name varchar(150)

);
create table Orders(
    order_id int primary key auto_increment,
    customer_name varchar(255),
    order_date date
);
create table OrderDetails(
    detail_id int primary key auto_increment,
    order_id int,
    foreign key (order_id) references Orders(order_id),
    product_name varchar(255),
    quantity int
);

create table departments (
    id int primary key auto_increment,
    name varchar(255) not null
);

create table employees (
    id int primary key auto_increment,
    name varchar(255) not null,
    department_id int,
    foreign key (department_id) references departments(id)
);

DELIMITER //
create procedure create_user(
    name_in varchar(100),
    email_in varchar(255)
)
begin
    INSERT INTO users(name,email)
    values (name_in,email_in);
end //
DELIMITER ;

DELIMITER //
create procedure transfer_money(
     from_account_id int,
     to_account_id int,
     amount decimal(15,2)
)
begin
    declare from_balance decimal(15,2);
    declare count_from int default 0;
    declare count_to int default 0;
    select count(*) into count_from
    from accounts
    where id = from_account_id;

    if count_from = 0 then
        signal sqlstate '45000'
        set message_text = 'Tài khoản gửi không tồn tại';
    end if;

    select count(*) into count_to
    from accounts
    where id = to_account_id;

    if count_to = 0 then
        signal sqlstate '45000'
        set message_text = 'Tài khoản nhận không tồn tại';
    end if;
    select balance into from_balance from accounts
    where id = from_account_id;

    if from_balance < amount then
        signal sqlstate '45000'
        set message_text = 'Không đủ số dư để chuyển tiền';
    end if;

    update accounts
    set balance = balance - amount
    where id = from_account_id;

    update accounts
    set balance = balance + amount
    where id = to_account_id;
end //
DELIMITER ;

DELIMITER //

create procedure bank_transfer(
    in from_id int,
    in from_name varchar(100),
    in from_bank varchar(100),
    in to_id int,
    in to_name varchar(100),
    in to_bank varchar(100),
    in amount decimal(15,2)
)
begin
    declare from_balance decimal(15,2);
    declare count_from int;
    declare count_to int;

    select count(*) into count_from
    from bank_accounts
    where account_id = from_id
      and account_name = from_name
      and bank_name = from_bank;

    if count_from = 0 then
        signal sqlstate '45000'
        set message_text = 'tài khoản gửi không tồn tại hoặc thông tin không đúng';
    end if;

    select count(*) into count_to
    from bank_accounts
    where account_id = to_id
      and account_name = to_name
      and bank_name = to_bank;

    if count_to = 0 then
        signal sqlstate '45000'
        set message_text = 'tài khoản nhận không tồn tại hoặc thông tin không đúng';
    end if;

    select balance into from_balance
    from bank_accounts
    where account_id = from_id;

    if from_balance < amount then
        signal sqlstate '45000'
        set message_text = 'không đủ số dư trong tài khoản gửi';
    end if;

    update bank_accounts
    set balance = balance - amount
    where account_id = from_id;

    update bank_accounts
    set balance = balance + amount
    where account_id = to_id;

end //
DELIMITER ;

DELIMITER //

create procedure create_order(
    in p_customer_name varchar(255),
    out p_order_id int
)
begin
    insert into orders(customer_name, order_date)
    values (p_customer_name, current_date());

    set p_order_id = last_insert_id();
end ;
create procedure add_order_detail(
    in p_order_id int,
    in p_product_name varchar(255),
    in p_quantity int
)
begin
    if p_quantity <= 0 then
        signal sqlstate '45000'
            set message_text = 'Số lượng phải lớn hơn 0';
    end if;
    insert into orderdetails(order_id, product_name, quantity)
    values ( p_order_id, p_product_name, p_quantity);
end //
DELIMITER ;

DELIMITER //
create procedure create_department(
    in p_name varchar(255),
    out p_department_id int
)
begin
    insert into departments(name) values (p_name);
    set p_department_id = last_insert_id();
end ;
create procedure add_employee(
    in p_name varchar(255),
    in p_department_id int
)
begin
    insert into employees(name, department_id)
    values ( p_name, p_department_id);
end//
DELIMITER ;
create table Orders1(
    order_id int primary key auto_increment,
    customer_name varchar(255),
    status varchar(50)
);
DELIMITER //
create procedure insert_order(
    in p_customer_name varchar(255),
    in p_status varchar(50)
)
begin
    insert into Orders1(customer_name, status)
    values (p_customer_name, p_status);
end ;
create procedure update_order_status(
    in p_order_id int,
    in p_new_status varchar(50)
)
begin
    update Orders1
    set status = p_new_status
    where order_id = p_order_id;
end ;
create procedure select_orders()
begin
    select * from Orders1;
end //
DELIMITER ;
create table rooms (
    room_id int primary key,
    room_type varchar(100),
    availability boolean,
    price decimal(10,2)
);

create table customers (
    customer_id int primary key,
    name varchar(255),
    phone varchar(20)
);

create table bookings (
    booking_id int primary key auto_increment,
    customer_id int,
    room_id int,
    booking_date datetime,
    status varchar(50),
    foreign key (customer_id) references customers(customer_id),
    foreign key (room_id) references rooms(room_id)
);

create table failed_bookings (
    log_id int primary key auto_increment,
    customer_name varchar(255),
    room_id int,
    reason varchar(255),
    log_time datetime default now()
);
DELIMITER //
create procedure check_room_availability(
    in p_room_id int,
    out p_available boolean
)
begin
    select availability into p_available
    from rooms
    where room_id = p_room_id;
end;
create procedure mark_room_unavailable(
    in p_room_id int
)
begin
    update rooms
    set availability = false
    where room_id = p_room_id;
end;
create procedure create_booking(
    in p_customer_id int,
    in p_room_id int,
    in p_status varchar(50)
)
begin
    insert into bookings(customer_id, room_id, booking_date, status)
    values (p_customer_id, p_room_id, now(), p_status);
end;
create procedure log_failed_booking(
    in p_customer_name varchar(255),
    in p_room_id int,
    in p_reason varchar(255)
)
begin
    insert into failed_bookings(customer_name, room_id, reason)
    values (p_customer_name, p_room_id, p_reason);
end //
DELIMITER ;
create table users1 (
    user_id int primary key,
    username varchar(100),
    balance decimal(10,2)
);

create table auctions (
    auction_id int primary key,
    item_name varchar(255),
    highest_bid decimal(10,2),
    status varchar(20)
);

create table bids (
    bid_id int primary key auto_increment,
    auction_id int,
    user_id int,
    bid_amount decimal(10,2),
    timestamp datetime default now(),
    foreign key (auction_id) references auctions(auction_id),
    foreign key (user_id) references users1(user_id)
);

create table failed_bids (
    fail_id int primary key auto_increment,
    user_id int,
    auction_id int,
    reason varchar(255),
    fail_time datetime default now()
);
DELIMITER //

create procedure check_user_balance(
    in p_user_id int,
    in p_bid_amount decimal(10,2),
    out p_enough boolean
)
begin
    declare user_balance decimal(10,2);

    select balance into user_balance from users1 where user_id = p_user_id;

    if user_balance >= p_bid_amount then
        set p_enough = true;
    else
        set p_enough = false;
    end if;
end;
create procedure get_current_highest_bid(
    in p_auction_id int,
    out p_highest_bid decimal(10,2)
)
begin
    select highest_bid into p_highest_bid
    from auctions
    where auction_id = p_auction_id;
end;
create procedure place_bid(
    in p_auction_id int,
    in p_user_id int,
    in p_bid_amount decimal(10,2)
)
begin
    update auctions
    set highest_bid = p_bid_amount
    where auction_id = p_auction_id;

    insert into bids(auction_id, user_id, bid_amount)
    values (p_auction_id, p_user_id, p_bid_amount);
end;
create procedure log_failed_bid(
    in p_user_id int,
    in p_auction_id int,
    in p_reason varchar(255)
)
begin
    insert into failed_bids(user_id, auction_id, reason)
    values (p_user_id, p_auction_id, p_reason);
end;
DELIMITER ;