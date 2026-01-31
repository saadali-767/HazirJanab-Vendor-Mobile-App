<?php
include("db_config.php");

try {
    // Prepare the SQL query to select all vendors
    $query = "SELECT * FROM vendor";
    $stmt = $dbh->prepare($query);

    // Execute the query
    $stmt->execute();

    // Fetch the results
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $response = array();

    if (!empty($result)) {
        $response["Status"] = 1;
        
        foreach ($result as &$vendor) {
            if (isset($vendor['picture'])) {
                // Convert BLOB data to base64
                $vendor['picture'] = base64_encode($vendor['picture']);
            }
        }
        
        $response["Vendors"] = $result;
    } else {
        $response["Status"] = 0;
        $response["Vendors"] = array();
        $response["Message"] = "No vendors found.";
    }

    echo json_encode($response);

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "error" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
