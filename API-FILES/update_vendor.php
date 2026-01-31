<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");
$data = json_decode(file_get_contents('php://input'), true);
error_log(print_r($data, true));

$response = array();

// Check if all required fields are set
if (isset($data['id']) && isset($data['first_name']) && isset($data['last_name']) && isset($data['email']) && isset($data['phone_number']) && isset($data['password']) && isset($data['address'])) {
    // Process the data
    $id = mysqli_real_escape_string($conn, $data['id']);
    $first_name = mysqli_real_escape_string($conn, $data['first_name']);
    $last_name = mysqli_real_escape_string($conn, $data['last_name']);
    $email = mysqli_real_escape_string($conn, $data['email']);
    $phone_number = mysqli_real_escape_string($conn, $data['phone_number']);
    $hashed_password = mysqli_real_escape_string($conn, password_hash($data['password'], PASSWORD_DEFAULT));
    $address = mysqli_real_escape_string($conn, $data['address']);

    $conn->begin_transaction();
    try {
        $stmt = $conn->prepare("UPDATE `vendor` SET `first_name`=?, `last_name`=?, `email`=?, `phone_number`=?, `password`=?, `Address`=? WHERE `id`=?");
        $stmt->bind_param("ssssssi", $first_name, $last_name, $email, $phone_number, $hashed_password, $address, $id);

        if (!$stmt->execute()) {
            throw new Exception("Error updating user information: " . $stmt->error);
        }

        if ($stmt->affected_rows == 0) {
            throw new Exception("No changes made to user information.");
        }

        $conn->commit();
        $response['Status'] = 1;
        $response['Message'] = "User information updated successfully";
    } catch (Exception $e) {
        $conn->rollback();
        $response['Status'] = 0;
        $response['Message'] = $e->getMessage();
    }

    $stmt->close();
} else {
    $response['Status'] = 0;
    $response['Message'] = "Required fields are missing";
}

header('Content-Type: application/json');
echo json_encode($response);

mysqli_close($conn);
?>
