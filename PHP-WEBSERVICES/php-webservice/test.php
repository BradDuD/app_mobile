<?php 

$mysqli = new mysqli("127.0.0.1", "root", "", "post_db");

// Verificar la conexión
if ($mysqli->connect_error) {
    die('Error de Conexión (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
}

echo 'Conexión exitosa... ' . $mysqli->host_info . "\n";

// Preparar la consulta de inserción
$stmt = $mysqli->prepare("INSERT INTO usuarios (login, password, nickname, email) VALUES (?, ?, ?, ?)");

if (!$stmt) {
    die('Error en la preparación de la consulta: ' . $mysqli->error);
}

// Enlazar parámetros
$login = "u1";
$password = password_hash("123", PASSWORD_BCRYPT); // Encriptar la contraseña
$nickname = "nick12";
$email = "email1@ejemplo.com";

$stmt->bind_param("ssss", $login, $password, $nickname, $email);

// Ejecutar la consulta
if ($stmt->execute()) {
    echo "Registro insertado correctamente.\n";
} else {
    echo "Error al insertar el registro: " . $stmt->error . "\n";
}

// Cerrar la consulta y la conexión
$stmt->close();
$mysqli->close();
?>