SELECT s.SupplierName, p.ProductName FROM Suppliers s
JOIN Products p ON s.SupplierID = p.SupplierID
ORDER BY s.SupplierName ASC, p.ProductName ASC;
