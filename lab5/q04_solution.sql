SELECT DISTINCT p.ProductName FROM Products p
JOIN Orders o ON p.ProductID = o.ProductID
WHERE o.Quantity > 4;
