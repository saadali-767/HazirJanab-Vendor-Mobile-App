<?php
// Database configuration
$dbHost     = 'localhost'; // your database host
$dbUsername = 'root'; // your database username
$dbPassword = ''; // your database password
$dbName     = 'hazirjanab'; // your database name

// Connect to the database
$db = new PDO("mysql:host=$dbHost;dbname=$dbName;charset=utf8", $dbUsername, $dbPassword);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
file_put_contents('php_input.log', print_r($_POST, true));

// Check if the necessary data is present in the post request
if (isset($_POST['user_id'], $_POST['service_id'], $_POST['vendor_id'], $_POST['city'], $_POST['address'], $_POST['description'], $_POST['image'],$_POST['type'])) {
    // Get data from POST request
    $userId = $_POST['user_id'];
    $serviceId = $_POST['service_id'];
    $vendorId = $_POST['vendor_id'];
    $city = $_POST['city'];
    $address = $_POST['address'];
    $description = $_POST['description'];
    $encodedImage = $_POST['image'];
    $type = $_POST['type'];


    // Decode the image
    $decodedImage = base64_decode($encodedImage);

    try {
        // SQL query to insert data into booking_emergency table
        $stmt = $db->prepare("INSERT INTO booking_emergency (user_id, service_id, vendor_id, city, address, description, image,type) VALUES (:user_id, :service_id, :vendor_id, :city, :address, :description, :image,:type)");

        // Bind parameters to the query
        $stmt->bindParam(':user_id', $userId);
        $stmt->bindParam(':service_id', $serviceId);
        $stmt->bindParam(':vendor_id', $vendorId);
        $stmt->bindParam(':city', $city);
        $stmt->bindParam(':address', $address);
        $stmt->bindParam(':description', $description);
        $stmt->bindParam(':image', $decodedImage, PDO::PARAM_LOB);
        $stmt->bindParam(':type', $type);


        // Execute the query
        $stmt->execute();

        // Success message
        echo json_encode(['status' => 'success', 'message' => 'Data inserted successfully']);
    } catch(PDOException $e) {
        // Error message
        echo json_encode(['status' => 'error', 'message' => 'Insert failed: ' . $e->getMessage()]);
    }
} else {
    // Missing data message
    echo json_encode(['status' => 'error', 'message' => 'Incomplete data provided']);
}
?>
