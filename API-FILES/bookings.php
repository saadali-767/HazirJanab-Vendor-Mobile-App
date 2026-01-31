<?php
include("db_config.php");

try {
    // Check if vendor_id parameter is provided in the request
    if (!isset($_POST['vendor_id'])) {
        echo json_encode(array("Status" => 0, "error" => "Vendor ID not provided."));
        exit;
    }

    // Get the vendor ID from the request
    $vendorId = $_POST['vendor_id'];

    // Prepare the SQL query with a parameter for vendor_id
    $query = "SELECT * FROM booking_normal WHERE vendor_id = :vendor_id";
    $stmt = $dbh->prepare($query);

    // Bind the vendor ID parameter
    $stmt->bindParam(':vendor_id', $vendorId, PDO::PARAM_INT);

    // Execute the query
    $stmt->execute();

    // Fetch the results
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $response = array();

    if (!empty($result)) {
        $response["Status"] = 1;
        
        foreach ($result as &$booking) {
            if (isset($booking['picture'])) {
                // Convert BLOB data to base64
                $booking['picture'] = base64_encode($booking['picture']);
            }
        }
        
        $response["Bookings"] = $result;
    } else {
        $response["Status"] = 0;
        $response["Bookings"] = array();
        $response["Message"] = "No bookings found for vendor ID: $vendorId.";
    }

    echo json_encode($response);

} catch (PDOException $e) {
    // Catch any error and output as JSON
    echo json_encode(array("Status" => 0, "error" => "Could not execute query. Error: " . $e->getMessage()));
}
?>
