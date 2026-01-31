package com.example.hazirjanabvendorportal;

import java.util.ArrayList;
import java.util.List;

public class ProductDataHolder {
    private static List<Product> productList = new ArrayList<>();

    // Method to get the list of products
    public static List<Product> getProductList() {
        return productList;
    }

    // Method to set a new list of products
    public static void setProductList(List<Product> newList) {
        productList = newList;
    }

    // Method to clear the product list
    public static void clearProductList() {
        productList.clear();
    }

    // Method to find a product by its ID
    public static Product findProductById(int productId) {
        for (Product product : productList) {
            if (product.getId() == productId) {
                return product;
            }
        }
        return null; // Return null if no matching product is found
    }

    // Additional methods to manipulate productList as necessary
}

