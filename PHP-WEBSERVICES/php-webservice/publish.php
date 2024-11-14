<?php
$mysqli = new mysqli("127.0.0.1", "root", "", "post_db");

if ($mysqli->connect_error) {
    die('Error de Conexión (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
}

header('Content-Type: application/json');

// Recibir los datos del post
$titulo = $_POST['titulo'] ?? '';
$contenido = $_POST['contenido'] ?? '';
$categoria_id = isset($_POST['categoria_id']) ? (int)$_POST['categoria_id'] : 0;
$usuario_id = isset($_POST['usuario_id']) ? (int)$_POST['usuario_id'] : 0;

// Validar que todos los campos están completos
if (empty($titulo) || empty($contenido) || $categoria_id <= 0 || $usuario_id <= 0) {
    echo json_encode(["error" => "Todos los campos son obligatorios y deben ser válidos"]);
    exit();
}

// Insertar el post en la base de datos con la fecha de publicación actual
$query = "INSERT INTO posts (titulo, contenido, categoria_id, usuario_id, fecha_publicacion) VALUES (?, ?, ?, ?, NOW())";
$stmt = $mysqli->prepare($query);

if ($stmt === false) {
    echo json_encode(["error" => "Error en la preparación de la consulta: " . $mysqli->error]);
    exit();
}

$stmt->bind_param("ssii", $titulo, $contenido, $categoria_id, $usuario_id);

if ($stmt->execute()) {
    echo json_encode(["success" => "Post publicado exitosamente"]);
} else {
    echo json_encode(["error" => "Error al publicar el post: " . $stmt->error]);
}

$stmt->close();
$mysqli->close();
?>
