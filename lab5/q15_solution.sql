SELECT City, SUM(CustomerCount) AS CustomerCount, SUM(SupplierCount) AS SupplierCount
FROM (
    SELECT City, COUNT(*) AS CustomerCount, 0 AS SupplierCount FROM Customers GROUP BY City
    UNION ALL
    SELECT City, 0, COUNT(*) FROM Suppliers GROUP BY City
) AS Combined
GROUP BY City
ORDER BY City ASC;
