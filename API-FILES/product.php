<?php
include("db_config.php");

try {
    // Modify the SELECT query to match the product table structure
    $query = "SELECT id, name, description, quantity, category, price, availability FROM product";
    $stmt = $dbh->prepare($query);

    // Execute the query
    $stmt->execute();

    // Fetch all the results
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Prepare the response array
    $response = array();

    // Check if any rows are returned
    if (!empty($result)) {
        $response["Status"] = 1; // Status for success
        $response["Products"] = $result; // Assign fetched products to "Products" key
    } else {
        $response["Status"] = 0; // Status for no products found
        $response["Products"] = array(); // Empty array for "Products"
        $response["Message"] = "No products found."; // Message key for no products
    }

    // Output the response as a JSON array
    echo json_encode($response);

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "error" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
