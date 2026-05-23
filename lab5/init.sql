DROP TABLE IF EXISTS Orders;
DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS Suppliers;
DROP TABLE IF EXISTS Customers;

CREATE TABLE Suppliers (
    SupplierID INT PRIMARY KEY,
    SupplierName VARCHAR(100) NOT NULL,
    City VARCHAR(50) NOT NULL,
    ContactEmail VARCHAR(100) NOT NULL
);

CREATE TABLE Products (
    ProductID INT PRIMARY KEY,
    ProductName VARCHAR(100) NOT NULL,
    Price FLOAT NOT NULL,
    StockQuantity INT NOT NULL,
    SupplierID INT,
    FOREIGN KEY (SupplierID) REFERENCES Suppliers(SupplierID)
);

CREATE TABLE Customers (
    CustomerID INT PRIMARY KEY,
    CustomerName VARCHAR(100) NOT NULL,
    City VARCHAR(50) NOT NULL,
    Email VARCHAR(100) NOT NULL
);

CREATE TABLE Orders (
    OrderID INT PRIMARY KEY,
    CustomerID INT,
    ProductID INT,
    OrderDate DATE NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID)
);

INSERT INTO Suppliers VALUES (101, 'Tech Supplies Ltd', 'Tel Aviv', 'contact@techsupplies.com');
INSERT INTO Suppliers VALUES (102, 'Mobile World', 'Haifa', 'sales@mobileworld.com');
INSERT INTO Suppliers VALUES (103, 'Office Essentials', 'Jerusalem', 'info@officeessentials.com');
INSERT INTO Suppliers VALUES (104, 'Gadget Hub', 'Tel Aviv', 'support@gadgethub.com');

INSERT INTO Products VALUES (1, 'Laptop', 5000, 15, 101);
INSERT INTO Products VALUES (2, 'Smartphone', 3000, 25, 102);
INSERT INTO Products VALUES (3, 'Keyboard', 150, 50, 103);
INSERT INTO Products VALUES (4, 'Monitor', 1200, 10, 101);
INSERT INTO Products VALUES (5, 'Mouse', 100, 60, 104);
INSERT INTO Products VALUES (6, 'Headphones', 250, 30, 102);
INSERT INTO Products VALUES (7, 'Printer', 800, 5, 103);
INSERT INTO Products VALUES (8, 'Desk Chair', 700, 20, 104);
INSERT INTO Products VALUES (9, 'External Hard Drive', 400, 18, 101);
INSERT INTO Products VALUES (10, 'Webcam', 300, 40, 102);

INSERT INTO Customers VALUES (201, 'Alice Cohen', 'Tel Aviv', 'alice@gmail.com');
INSERT INTO Customers VALUES (202, 'Bob Levy', 'Haifa', 'bob@levy.com');
INSERT INTO Customers VALUES (203, 'Carol Segal', 'Jerusalem', 'carol@segal.org');
INSERT INTO Customers VALUES (204, 'David King', 'Tel Aviv', 'david.king@kingmail.com');
INSERT INTO Customers VALUES (205, 'Eve Green', 'Be''er Sheva', 'eve.green@greenmail.co.il');

INSERT INTO Orders VALUES (301, 201, 1, '2024-01-10', 1);
INSERT INTO Orders VALUES (302, 202, 2, '2024-02-05', 2);
INSERT INTO Orders VALUES (303, 203, 3, '2024-03-15', 5);
INSERT INTO Orders VALUES (304, 204, 4, '2024-01-18', 1);
INSERT INTO Orders VALUES (305, 205, 5, '2024-02-22', 3);
INSERT INTO Orders VALUES (306, 201, 6, '2024-01-25', 2);
INSERT INTO Orders VALUES (307, 202, 7, '2024-02-10', 1);
INSERT INTO Orders VALUES (308, 203, 8, '2024-03-01', 1);
INSERT INTO Orders VALUES (309, 204, 9, '2024-01-30', 1);
INSERT INTO Orders VALUES (310, 205, 10, '2024-03-10', 4);
