<?php
$mysqli = new mysqli("127.0.0.1", "root", "", "post_db");

if ($mysqli->connect_error) {
    die('Error de Conexión (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
}

header('Content-Type: application/json');

$username = $_POST['username'];
$nickname = $_POST['nickname'];
$email = $_POST['email'];
$password = $_POST['password']; 
// $password = password_hash($_POST['password'], PASSWORD_DEFAULT); // Encriptar contraseña

$query = "INSERT INTO usuarios (login, nickname, email, password) VALUES (?, ?, ?, ?)";

$stmt = $mysqli->prepare($query);
$stmt->bind_param("ssss", $username, $nickname, $email, $password);
$result = $stmt->execute();

if ($result) {
    echo json_encode(["message" => "Usuario registrado con éxito"]);
} else {
    echo json_encode(["error" => $stmt->error]);
}

$stmt->close();
$mysqli->close();
?>
