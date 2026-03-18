# MiniShop Electronics - Project Specification

## Overview

A simplified electronics shop system designed for university use, allowing customers to browse, filter, and purchase electronic products like phones, laptops, tablets, and accessories

## Scope

This is a university project focused on core e-commerce functionality without complex administrative features. The system emphasizes user interactions with the product catalog and order management.

## Features

### F1. Import Functionality

- **Products and Users**: All product inventory and user accounts are created and updated via import from CSV/JSON files
- **No Admin Interface**: There is no seller or administrator role; all data management happens through file imports
- **Batch Updates**: Changes to product stock, prices, or user information are applied by importing updated files

### F2. Authentication

- **Register**: New customers can create an account with name, email, and password
- **Login**: Users authenticate with their credentials to access the system
- **Logout**: Users can securely log out of their session

### F3. Product Browsing

- **View Available Products**: Users can see a complete list of products available in the shop
- **Search Options**
- **Filter Options**: Products can be filtered by:
  - Product name
  - Category (phones, laptops, tablets, accessories)
  - Brand (Samsung, Apple, Lenovo, etc.)
  - Price range (min - max)
- **Sort Options**: Products can be sorted by:
  - Price (ascending/descending)
  - Name (alphabetical)

### F4. Product Purchase

- **Shopping Cart Flow**: The purchase process follows a standard shopping cart pattern:
  - Users can add available products to their cart
  - Users can adjust quantities in the cart
  - Users can review their cart before finalizing the order
  - Users can remove products from the cart before checkout
  - A "checkout" action finalizes the order for all products in the cart
- **Stock Check**: Products can only be added to cart if they are currently in stock
- **Quantity Validation**: Users cannot add more units than available in stock
- **Price Lock**: The price shown at the time of adding to cart is the price the user pays at checkout

### F5. Notifications

- **Order Confirmation**: After checkout, users receive a confirmation with order details and estimated delivery date
