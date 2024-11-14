<?php
$mysqli = new mysqli("127.0.0.1", "root", "", "post_db");

if ($mysqli->connect_error) {
    die('Error de Conexión (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
}

header('Content-Type: application/json');

$login = $_POST['username'] ?? '';
$password = $_POST['password'] ?? '';

if (empty($login) || empty($password)) {
    echo json_encode(["error" => "Usuario o contraseña no pueden estar vacíos"]);
    exit();
}


$query = "SELECT id, password FROM usuarios WHERE login = ?";
$stmt = $mysqli->prepare($query);
$stmt->bind_param("s", $login);
$stmt->execute();
$stmt->store_result();
$stmt->bind_result($userId, $storedPassword); 
$stmt->fetch();

if ($stmt->num_rows > 0) {
    
    if ($password === $storedPassword) {
        // Si las contraseñas coinciden, devolver el ID y el mensaje de éxito
        echo json_encode([
            "success" => "Login exitoso",
            "user_id" => $userId // Agrega el ID del usuario a la respuesta
        ]);
    } else {
        echo json_encode(["error" => "Contraseña incorrecta"]);
    }
} else {
    echo json_encode(["error" => "Usuario no encontrado"]);
}

$stmt->close();
$mysqli->close();
?>
