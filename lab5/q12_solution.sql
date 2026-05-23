SELECT c.CustomerName, SUM(o.Quantity) AS TotalQuantity FROM Customers c
JOIN Orders o ON c.CustomerID = o.CustomerID
GROUP BY c.CustomerID, c.CustomerName
ORDER BY TotalQuantity DESC;
