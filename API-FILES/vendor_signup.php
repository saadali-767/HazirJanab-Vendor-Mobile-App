<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");

$data = json_decode(file_get_contents('php://input'), true);
error_log(print_r($data, true)); // Log the contents of $data

$response = array();


// *****************ENCRYPTION*****************
$ENCRYPTION_KEY = getenv('ENCRYPTION_KEY');

function encrypt($data, $key) {
    $iv = openssl_random_pseudo_bytes(openssl_cipher_iv_length('aes-256-cbc'));
    $encrypted = openssl_encrypt($data, 'aes-256-cbc', $key, 0, $iv);
    return base64_encode($encrypted . '::' . $iv);
}

function encryptBlob($data, $key) {
    $iv = openssl_random_pseudo_bytes(openssl_cipher_iv_length('aes-256-cbc'));
    $encrypted = openssl_encrypt($data, 'aes-256-cbc', $key, OPENSSL_RAW_DATA, $iv);
    return base64_encode($iv . $encrypted);
}


// *****************ENCRYPTION*****************



// Check if $data is not null and is an array
if (!is_null($data) && is_array($data)) {
    $requiredFields = ['first_name', 'last_name', 'gender', 'CNIC', 'password', 'email', 'phone_number', 'address', 'vendor_picture', 'cnic_front', 'cnic_back', 'total_rating', 'aspect_time', 'aspect_quality', 'aspect_expertise', 'category'];
    $missingFields = array_diff($requiredFields, array_keys($data));
    if (!empty($missingFields)) {
        $response['Status'] = 0;
        $response['Message'] = "Required fields are missing: " . implode(', ', $missingFields);
    } else {
        // Process the data
        error_log("Received data: " . print_r($data, true)); // Debugging statement
        $first_name = mysqli_real_escape_string($conn, $data['first_name']);
        $last_name = mysqli_real_escape_string($conn, $data['last_name']);
        $gender = mysqli_real_escape_string($conn, $data['gender']);
        $encrypted_cnic = encrypt($data['CNIC'], $ENCRYPTION_KEY);
        $hashed_password = mysqli_real_escape_string($conn, password_hash($data['password'], PASSWORD_DEFAULT));
        $email = mysqli_real_escape_string($conn, $data['email']);
        $phone_number = mysqli_real_escape_string($conn, $data['phone_number']);
        $address = mysqli_real_escape_string($conn, $data['address']);
    
        // Debugging statements for received data
        error_log("First Name: $first_name, Last Name: $last_name, Gender: $gender, CNIC: $encrypted_cnic, Password: $hashed_password, Email: $email, Phone Number: $phone_number, Address: $address");

        // Decode Base64 encoded images
        $vendor_picture_decoded = base64_decode($data['vendor_picture']);
        $cnic_front_decoded = base64_decode($data['cnic_front']);
        $cnic_back_decoded = base64_decode($data['cnic_back']);

        // Debugging statements for decoded images
        error_log("Vendor Picture Decoded: " . substr($vendor_picture_decoded, 0, 100)); // Display only first 100 characters
        error_log("\nCNIC Front Decoded: " . substr($cnic_front_decoded, 0, 100)); // Display only first 100 characters
        error_log("\nCNIC Back Decoded: " . substr($cnic_back_decoded, 0, 100)); // Display only first 100 characters

        $encrypted_vendor_picture = encryptBlob($vendor_picture_decoded, $ENCRYPTION_KEY);
        $encrypted_cnic_front = encryptBlob($cnic_front_decoded, $ENCRYPTION_KEY);
        $encrypted_cnic_back = encryptBlob($cnic_back_decoded, $ENCRYPTION_KEY);

        error_log("Vendor Picture Encrypted: " . substr($encrypted_vendor_picture, 0, 100)); // Display only first 100 characters for debugging
        error_log("CNIC Front Encrypted: " . substr($encrypted_cnic_front, 0, 100)); // Display only first 100 characters for debugging
        error_log("CNIC Back Encrypted: " . substr($encrypted_cnic_back, 0, 100)); // Display only first 100 characters for debugging

    
        // Prepare and bind parameters to insert statement
        $sql = "INSERT INTO vendor (first_name, last_name, gender, CNIC, password, email, phone_number, address, picture, cnic_front, cnic_back, total_rating, aspect_time, aspect_quality, aspect_expertise, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssssssssssdddds", $first_name, $last_name, $gender, $encrypted_cnic, $hashed_password, $email, $phone_number, $address, $encrypted_vendor_picture, $encrypted_cnic_front, $encrypted_cnic_back , $data['total_rating'], $data['aspect_time'], $data['aspect_quality'], $data['aspect_expertise'], $data['category']);
    
        // Execute the insert statement
        if ($stmt->execute()) {
            $response['Status'] = 1;
            $response['Message'] = "Vendor signed up successfully";
        } else {
            $response['Status'] = 0;
            $response['Message'] = "Error signing up vendor: " . $stmt->error;
        }
    
        // Close statement
        $stmt->close();
    }
    
    // Send JSON response
    header('Content-Type: application/json');
    echo json_encode($response);
}    