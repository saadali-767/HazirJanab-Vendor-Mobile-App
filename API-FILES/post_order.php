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

// Check if the necessary data is present in the POST request
if (isset($_POST['user_id'], $_POST['service_id'], $_POST['vendor_id'], $_POST['city'], $_POST['address'], $_POST['date'], $_POST['time'], $_POST['description'], $_POST['type'], $_POST['status'])) {
    // Get data from POST request
    $user_id = $_POST['user_id'];
    $service_id = $_POST['service_id'];
    $vendor_id = $_POST['vendor_id'];
    $city = $_POST['city'];
    $address = $_POST['address'];
    $date = $_POST['date'];
    $time = $_POST['time'];
    $description = $_POST['description'];
    $type = $_POST['type'];
    $status = $_POST['status'];

    // Handle the picture if it's included in the POST request
    $picture = NULL;
    if (isset($_POST['image'])) {
        $encodedImage = $_POST['image'];
        $picture = base64_decode($encodedImage);
    }

    try {
        // SQL query to insert data into booking_normal table
        $stmt = $db->prepare("INSERT INTO booking_normal (user_id, service_id, vendor_id, city, address, date, time, description, picture, type, status) VALUES (:user_id, :service_id, :vendor_id, :city, :address, :date, :time, :description, :picture, :type, :status)");

        // Bind parameters to the query
        $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt->bindParam(':service_id', $service_id, PDO::PARAM_INT);
        $stmt->bindParam(':vendor_id', $vendor_id, PDO::PARAM_INT);
        $stmt->bindParam(':city', $city);
        $stmt->bindParam(':address', $address);
        $stmt->bindParam(':date', $date);
        $stmt->bindParam(':time', $time);
        $stmt->bindParam(':description', $description);
        $stmt->bindParam(':type', $type);
        $stmt->bindParam(':status', $status);
        
        // Bind the picture as a blob if it's included
        if ($picture !== NULL) {
            $stmt->bindParam(':picture', $picture, PDO::PARAM_LOB);
        } else {
            $stmt->bindValue(':picture', NULL, PDO::PARAM_NULL);
        }

        // Execute the query
        $stmt->execute();

        // Get the last inserted ID
        $bookingId = $db->lastInsertId();

        // Success message with booking ID
        echo json_encode(['status' => 'success', 'message' => 'Data inserted successfully', 'booking_id' => $bookingId]);
    } catch (PDOException $e) {
        // Error message
        echo json_encode(['status' => 'error', 'message' => 'Insert failed: ' . $e->getMessage()]);
    }
} else {
    // Missing data message
    echo json_encode(['status' => 'error', 'message' => 'Incomplete data provided']);
}
?>
