<?php

include("db_config.php");
$response = array();

if(isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['email']) && isset($_POST['password']) && isset($_POST['address']) ){
    $first_name = $_POST['first_name'];
    $last_name = $_POST['last_name'];
    $email = $_POST['email'];
    $password = $_POST['password'];
    $address = $_POST['address'];

    $sql = "INSERT INTO `user`(`first_name`,`last_name`, `email`, `password`, `address`) VALUES ('$first_name','$last_name', '$email', '$password', '$address')";

    $result = mysqli_query($conn, $sql);

    if($result){
        $response['Status'] = 1;
        $response['id'] = mysqli_insert_id($conn);
        $response['Message'] = "Data inserted successfully";
    }
    else{
        $response['Status'] = 0;
        $response['Message'] = "Data insertion failed";
    }
    echo json_encode($response);
}
else{
    $response['Status'] = 0;
    $response['Message'] = "Required fields missing";
    echo json_encode($response);
}
?>
