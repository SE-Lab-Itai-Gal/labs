SELECT p.ProductName FROM Products p
JOIN Suppliers s ON p.SupplierID = s.SupplierID
WHERE s.SupplierName LIKE '%s%'
UNION
SELECT p.ProductName FROM Products p
JOIN Suppliers s ON p.SupplierID = s.SupplierID
WHERE s.SupplierName LIKE '%S%';
