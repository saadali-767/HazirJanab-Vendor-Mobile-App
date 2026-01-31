<?php
include("db_config.php");

$query = "SELECT id, img FROM item"; // Get only the IDs and images to reduce memory usage
$stmt = $dbh->prepare($query);
$response = array();

if ($stmt->execute()) {
    $items = $stmt->fetchAll(PDO::FETCH_ASSOC);
    if (!empty($items)) {
        foreach ($items as $index => $item) {
            if ($item['img'] !== null) {
                // Convert the binary data to base64
                $items[$index]['img'] = base64_encode($item['img']);
            } else {
                $items[$index]['img'] = null; // or however you wish to handle null images
            }
        }
        $response['Status'] = 1;
        $response['Message'] = "Images retrieved successfully";
        $response['Images'] = $items;
    } else {
        $response['Status'] = 0;
        $response['Message'] = "No images found.";
    }
} else {
    $response['Status'] = 0;
    $response['Message'] = "Error executing query.";
    $response['Error'] = $stmt->errorInfo();
}

header('Content-Type: application/json');
echo json_encode($response);
?>
