<?php
// Database configuration
$dbHost     = 'localhost'; // your database host
$dbUsername = 'root'; // your database username
$dbPassword = ''; // your database password
$dbName     = 'hazirjanab'; // your database name

// Connect to the database
$db = new PDO("mysql:host=$dbHost;dbname=$dbName;charset=utf8", $dbUsername, $dbPassword);
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// Check if the necessary data is present in the post request
if (isset($_POST['city'], $_POST['user_id'], $_POST['service_id'],$_POST['type'],$_POST['vendor_id'],$_POST['address'], $_POST['date'], $_POST['time'], $_POST['description'], $_POST['image'])) {
    // Get data from POST request
    $user_id = $_POST['user_id'];
    $service_id = $_POST['service_id'];
    $vendor_id = $_POST['vendor_id'];
    $city = $_POST['city'];
    $address = $_POST['address'];
    $date = $_POST['date'];
    $time = $_POST['time'];
    $description = $_POST['description'];
    $encodedImage = $_POST['image'];
    $type = $_POST['type'];


    // Decode the image
    $decodedImage = base64_decode($encodedImage);

    try {
        // SQL query to insert data into booking_normal table
        $stmt = $db->prepare("INSERT INTO booking_normal (user_id,service_id,vendor_id,city, address, date, time, description, picture,type) VALUES (:user_id,:service_id,:vendor_id,:city, :address, :date, :time, :description, :picture,:type)");

        // Bind parameters to the query
        $stmt->bindParam(':user_id', $user_id);
        $stmt->bindParam(':service_id', $service_id);
        $stmt->bindParam(':vendor_id', $vendor_id);


        $stmt->bindParam(':city', $city);
        $stmt->bindParam(':address', $address);
        $stmt->bindParam(':date', $date);
        $stmt->bindParam(':time', $time);
        $stmt->bindParam(':description', $description);
        $stmt->bindParam(':picture', $decodedImage, PDO::PARAM_LOB);
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
