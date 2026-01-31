<?php
include 'db_config.php'; // Modify as needed for your configuration

$response = ['Status' => 0]; // Default response if no review is found or error occurs

$user_id = isset($_POST['user_id']) ? (int)$_POST['user_id'] : null;
$vendor_id = isset($_POST['vendor_id']) ? (int)$_POST['vendor_id'] : null;

if ($user_id && $vendor_id) {
    try {
        $stmt = $dbh->prepare("SELECT COUNT(*) FROM reviews WHERE user_id = :user_id AND vendor_id = :vendor_id");
        $stmt->execute(['user_id' => $user_id, 'vendor_id' => $vendor_id]);
        $count = $stmt->fetchColumn();

        if ($count > 0) {
            $response['Status'] = 1; // Review exists
        }
    } catch (PDOException $e) {
        $response['error'] = $e->getMessage();
    }
} else {
    $response['error'] = 'Invalid user_id or vendor_id.';
}

echo json_encode($response);
?>
