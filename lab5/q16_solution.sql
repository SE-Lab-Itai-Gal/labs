SELECT DISTINCT c.CustomerName FROM Customers c
JOIN Orders o ON c.CustomerID = o.CustomerID
GROUP BY c.CustomerID, c.CustomerName, o.ProductID
HAVING COUNT(*) >= 2;
