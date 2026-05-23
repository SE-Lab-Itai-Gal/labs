SELECT DISTINCT s.SupplierName FROM Suppliers s
JOIN Products p ON s.SupplierID = p.SupplierID
WHERE p.Price > 1000;
