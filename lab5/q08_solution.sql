SELECT DISTINCT c.CustomerID, c.CustomerName, c.City, c.Email FROM Customers c
JOIN Orders o ON c.CustomerID = o.CustomerID
JOIN Products p ON o.ProductID = p.ProductID
JOIN Suppliers s ON p.SupplierID = s.SupplierID
WHERE c.City = s.City;
