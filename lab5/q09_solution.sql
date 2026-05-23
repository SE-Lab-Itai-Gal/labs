SELECT s.SupplierName, COUNT(p.ProductID) AS ProductCount FROM Suppliers s
JOIN Products p ON s.SupplierID = p.SupplierID
GROUP BY s.SupplierID, s.SupplierName;
