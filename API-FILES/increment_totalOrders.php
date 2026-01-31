<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php"); // Your DB configuration file
$data = json_decode(file_get_contents('php://input'), true);

$response = array();

// Check if all required fields are set
if (isset($data['id']) && isset($data['total_orders'])) {
    // Process the data
    $id = mysqli_real_escape_string($conn, $data['id']);
    $total_orders = mysqli_real_escape_string($conn, $data['total_orders']);

    $conn->begin_transaction();
    try {
        $stmt = $conn->prepare("UPDATE vendor SET total_orders=? WHERE id=?");
        $stmt->bind_param("ii", $total_orders, $id);

        if (!$stmt->execute()) {
            throw new Exception("Error updating total orders: " . $stmt->error);
        }

        if ($stmt->affected_rows == 0) {
            throw new Exception("No changes made to total orders.");
        }

        $conn->commit();
        $response['Status'] = 1;
        $response['Message'] = "Total orders updated successfully";
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
