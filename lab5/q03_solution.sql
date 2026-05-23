SELECT DISTINCT c.CustomerName FROM Customers c
JOIN Orders o ON c.CustomerID = o.CustomerID;
