<?php
// Database configuration
$dbHost     = 'localhost'; // your database host
$dbUsername = 'root'; // your database username
$dbPassword = ''; // your database password
$dbName     = 'hazirjanab'; // your database name

// Connect to the database using PDO
try {
    $db = new PDO("mysql:host=$dbHost;dbname=$dbName;charset=utf8", $dbUsername, $dbPassword);
    $db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Connection failed: " . $e->getMessage());
}

// Decode JSON from the request body
$input = file_get_contents('php://input');
$data = json_decode($input, true); // Decode as associative array

// Check if the necessary data is present in the decoded data
if (isset($data['bookings']) && is_array($data['bookings'])) {
    $bookings = $data['bookings'];

    try {
        $db->beginTransaction();

        // SQL query to insert data into product_booking table
        $stmt = $db->prepare("INSERT INTO product_booking (booking_id, product_id, quantity) VALUES (:booking_id, :product_id, :quantity)");

        // Loop through each booking and insert it into the database
        foreach ($bookings as $booking) {
            // Check if the necessary data is present for each booking
            if (isset($booking['booking_id'], $booking['product_id'], $booking['quantity'])) {
                // Bind parameters to the query
                $stmt->bindParam(':booking_id', $booking['booking_id'], PDO::PARAM_INT);
                $stmt->bindParam(':product_id', $booking['product_id'], PDO::PARAM_INT);
                $stmt->bindParam(':quantity', $booking['quantity'], PDO::PARAM_INT);

                // Execute the query
                $stmt->execute();
            } else {
                // If any booking does not have complete data, roll back and return an error
                $db->rollBack();
                echo json_encode(['status' => 'error', 'message' => 'Incomplete data provided for one or more bookings']);
                exit;
            }
        }

        // Commit the transaction
        $db->commit();

        // Success message
        echo json_encode(['status' => 'success', 'message' => 'All data inserted successfully']);
    } catch (PDOException $e) {
        $db->rollBack();
        // Error message
        echo json_encode(['status' => 'error', 'message' => 'Insert failed: ' . $e->getMessage()]);
    }
} else {
    // Missing data message
    echo json_encode(['status' => 'error', 'message' => 'No data provided']);
}
?>
