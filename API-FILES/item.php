<?php
include("db_config.php");

try {
    $query = "SELECT id, name, hourlyrate, description, city, category, type, img FROM item";
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
        
        // Process each item to include base64 encoding of img if present
        foreach ($result as &$item) {
            if (isset($item['img'])) {
                // Convert BLOB data to base64
                $item['img'] = base64_encode($item['img']);
            }
        }
        
        $response["Items"] = $result; // Assign fetched items to "Items" key
    } else {
        $response["Status"] = 0; // Status for no items found
        $response["Items"] = array(); // Empty array for "Items"
        $response["Message"] = "No items found."; // Message key for no items
    }

    // Output the response as a JSON array
    echo json_encode($response);

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "error" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
