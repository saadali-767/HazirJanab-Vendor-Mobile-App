<?php
require_once('stripe-php-master/init.php'); // Ensure this path points to your Stripe PHP library

// Set your secret API key
\Stripe\Stripe::setApiKey('sk_test_51MC7IZKE7yyXcUzGrBs4Bb3lBtpDcZ8JejigMZKjdTsFOX8GswPfnp6cfDfsjVnI88w8AIUU1LrjFdpBERBMnuZy00Aoe7vIWk');

header('Content-Type: application/json');

// Retrieving the amount and token from the POST data sent from the Android app
$token = isset($_POST['token']) ? $_POST['token'] : ''; // Replace 'tok_visa' with actual token from frontend
$amount = isset($_POST['amount']) ? $_POST['amount'] : 2000; // Default to $20, in cents, if no amount is provided
$currency = 'pkr';

$response = [];

try {
    $charge = \Stripe\Charge::create([
        'amount' => $amount, // Amount in cents
        'currency' => $currency,
        'source' => $token,
        'description' => 'Charge for a service or product'
    ]);

    // If the charge is successful
    $response['status'] = 'success';
    $response['charge_id'] = $charge->id;
    $response['amount'] = $charge->amount;
    $response['currency'] = $charge->currency;
} catch (\Stripe\Exception\ApiErrorException $e) {
    // Catch any API errors and respond with the error
    $response['status'] = 'error';
    $response['message'] = $e->getMessage();
}

// Send the response back to the Android application
echo json_encode($response);
?>
