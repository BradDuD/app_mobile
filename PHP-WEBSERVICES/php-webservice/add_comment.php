<?php

$conn = new mysqli("127.0.0.1", "root", "", "post_db");

// Verificar la conexión
if ($conn->connect_error) {
    die(json_encode(array("status" => "error", "message" => "Conexión fallida: " . $conn->connect_error)));
}

// Obtener datos del POST
$post_id = isset($_POST['post_id']) ? intval($_POST['post_id']) : null;
$user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : null;
$comment_text = isset($_POST['comment_text']) ? $_POST['comment_text'] : null;

// Validar datos
if ($post_id === null || $user_id === null || empty($comment_text)) {
    echo json_encode(array("status" => "error", "message" => "Datos incompletos"));
    exit;
}

// Preparar la consulta SQL para insertar el comentario
$stmt = $conn->prepare("INSERT INTO comentarios (cuerpo_comentario, usuario_id, post_id) VALUES (?, ?, ?)");
$stmt->bind_param("sii", $comment_text, $user_id, $post_id);

// Ejecutar la consulta y verificar el resultado
if ($stmt->execute()) {
    echo json_encode(array("status" => "success", "message" => "Comentario agregado exitosamente"));
} else {
    echo json_encode(array("status" => "error", "message" => "Error al agregar el comentario: " . $stmt->error));
}

// Cerrar la conexión
$stmt->close();
$conn->close();
?>
