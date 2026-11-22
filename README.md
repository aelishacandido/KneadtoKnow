# Knead to Know — Baking Supplies Inventory & POS System

**Knead to Know** is a Java Swing desktop inventory and point-of-sale (POS) system built for a local baking supply shop. Designed to run offline using custom binary file serialization (`.dat`), it combines role-based access control, multi-category product tracking, recursive search/alerts, and an auditable transaction ledger.

---

## Features

* **Role-Based Access Control (RBAC):** Polymorphic `User` hierarchy separating **Administrators** (`AdminUser` — full catalog CRUD, stock control, undo, and reports) from **Store Staff** (`StaffUser` — POS checkout, stock-in deliveries, and read-only views).
* **POS Checkout & Receipts:** Live shopping cart management with stock validation, discount calculation, change computation, and formatted receipt generation.
* **Multi-Category Product Catalog:** Tracks `RawIngredient` (weight and expiration dates), `BakingTool` (material and warranty), and `PackagingItem` (dimensions and pack size).
* **Recursive Operations & Audit Ledger:** Recursive product ID lookups and low-stock threshold alerts, paired with a `StockTransaction` ledger supporting one-click **Undo** and `.txt` report exports.

---

### Default Login Credentials
| Role | Username | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Full access (POS, Stock-In, Add/Edit/Delete Products, Undo, Reports) |
| **Store Staff** | `staff` | `staff123` | POS Checkout, Stock-In Deliveries, View Inventory & Analytics |

---