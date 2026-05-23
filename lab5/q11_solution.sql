SELECT c.CustomerName, p.ProductName, o.OrderDate FROM Orders o
JOIN Customers c ON o.CustomerID = c.CustomerID
JOIN Products p ON o.ProductID = p.ProductID
WHERE YEAR(o.OrderDate) = 2024;
