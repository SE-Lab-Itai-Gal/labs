SELECT ProductName FROM Products
WHERE Price = (SELECT MAX(Price) FROM Products);
