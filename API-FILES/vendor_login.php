<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include("db_config.php");
$response = array();

// *****************ENCRYPTION*****************
$ENCRYPTION_KEY = getenv('ENCRYPTION_KEY');
function decrypt($data, $key) {
    $parts = explode('::', base64_decode($data), 2);

    if (count($parts) === 2) {
        list($encrypted_data, $iv) = $parts;
    } else {
        throw new Exception('The data to decrypt is incorrectly formatted.');
    }

    if ($encrypted_data === false || $iv === false) {
        throw new Exception('The data to decrypt is incorrectly formatted or missing.');
    }

    return openssl_decrypt($encrypted_data, 'aes-256-cbc', $key, 0, $iv);
}

function decryptBlob($data, $key) {
    $rawData = base64_decode($data);
    $ivLength = openssl_cipher_iv_length('aes-256-cbc');
    $iv = substr($rawData, 0, $ivLength);
    $encryptedData = substr($rawData, $ivLength);
    return openssl_decrypt($encryptedData, 'aes-256-cbc', $key, OPENSSL_RAW_DATA, $iv);
}

// *****************ENCRYPTION*****************

if(isset($_POST['email']) && isset($_POST['password'])) {
    $email = $_POST['email'];
    $password = $_POST['password'];

    $sql = "SELECT * FROM `vendor` WHERE BINARY `email` = ?";

    if ($stmt = mysqli_prepare($conn, $sql)) {
        mysqli_stmt_bind_param($stmt, "s", $email);

        mysqli_stmt_execute($stmt);

        $result = mysqli_stmt_get_result($stmt);

        if(mysqli_num_rows($result) > 0) {
            $user = mysqli_fetch_assoc($result);
            if (password_verify($password, $user['password'])) {
                $user['password'] = $password;
                $user['CNIC'] = decrypt($user['CNIC'], $ENCRYPTION_KEY);
                if (isset($user['picture']) && isset($user['cnic_front']) && isset($user['cnic_back'])) {
                    
                    $decrypted_picture = decryptBlob($user['picture'], $ENCRYPTION_KEY);
                    $decrypted_cnic_front = decryptBlob($user['cnic_front'], $ENCRYPTION_KEY);
                    $decrypted_cnic_back = decryptBlob($user['cnic_back'], $ENCRYPTION_KEY);
                    
                    $user['picture'] = base64_encode($decrypted_picture);
                    $user['cnic_front'] = base64_encode($decrypted_cnic_front);
                    $user['cnic_back'] = base64_encode($decrypted_cnic_back);
                }
                $response['Status'] = 1;
                $response['Message'] = "Login successful";
                $response['User'] = $user;
            } else {
                $response['Status'] = 0;
                $response['Message'] = "Login failed. Please check your credentials.";
            }
        } else {
            $response['Status'] = 0;
            $response['Message'] = "Login failed. Please check your credentials.";
        }
    } else {
        $response['Status'] = 0;
        $response['Message'] = "Login failed due to an internal error.";
    }
} else {
    $response['Status'] = 0;
    $response['Message'] = "Required fields missing";
}

header('Content-Type: application/json');
echo json_encode($response);

?>
