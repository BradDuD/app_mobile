<?php
$mysqli = new mysqli("127.0.0.1", "root", "", "post_db");

// Verificar la conexión
if ($mysqli->connect_error) {
    die('Error de Conexión (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error);
}

header('Content-Type: application/json');

// Obtener el ID del post desde la solicitud
$post_id = isset($_GET['post_id']) ? intval($_GET['post_id']) : 0;

// Verificar si se proporcionó un ID de post válido
if ($post_id <= 0) {
    echo json_encode(["error" => "ID de post no válido"]);
    exit();
}

// Obtener detalles del post, comentarios y etiquetas
$query = "
    SELECT p.id, p.titulo, p.contenido, p.fecha_publicacion, c.nombre AS categoria, u.nickname AS autor,
    (SELECT COUNT(*) FROM comentarios WHERE post_id = p.id) AS comentarios_count
    FROM posts p
    JOIN categorias c ON p.categoria_id = c.id
    JOIN usuarios u ON p.usuario_id = u.id
    WHERE p.id = ?
";

$stmt = $mysqli->prepare($query);
$stmt->bind_param("i", $post_id);
$stmt->execute();
$post_result = $stmt->get_result()->fetch_assoc();

// Obtener comentarios
$comentarios_query = "SELECT c.cuerpo_comentario, u.nickname AS autor FROM comentarios c JOIN usuarios u ON c.usuario_id = u.id WHERE c.post_id = ?";
$stmt = $mysqli->prepare($comentarios_query);
$stmt->bind_param("i", $post_id);
$stmt->execute();
$comentarios_result = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);

// Obtener etiquetas
$etiquetas_query = "SELECT e.nombre_etiqueta FROM posts_etiquetas pe JOIN etiquetas e ON pe.etiqueta_id = e.id WHERE pe.post_id = ?";
$stmt = $mysqli->prepare($etiquetas_query);
$stmt->bind_param("i", $post_id);
$stmt->execute();
$etiquetas_result = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);

// Devolver los resultados
echo json_encode([
    "post" => $post_result,
    "comentarios" => $comentarios_result,
    "etiquetas" => $etiquetas_result
]);

$stmt->close();
$mysqli->close();
?>
