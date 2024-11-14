<?php
$mysqli = new mysqli("127.0.0.1", "root", "", "post_db");

// Verificar la conexión
if ($mysqli->connect_error) {
    die(json_encode(['error' => 'Error de Conexión (' . $mysqli->connect_errno . ') ' . $mysqli->connect_error]));
}

// Establecer el encabezado para devolver JSON
header('Content-Type: application/json');

// Realizar la consulta SQL para obtener los posts y contar los likes
$query = "SELECT p.id, p.titulo, p.contenido, p.fecha_publicacion, c.nombre_categoria AS categoria, 
                 u.nickname AS autor,
                 (SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count 
          FROM posts p 
          JOIN categorias c ON p.categoria_id = c.id
          JOIN usuarios u ON p.usuario_id = u.id
          ORDER BY p.fecha_publicacion DESC";

$result = $mysqli->query($query);

// Verificar si la consulta fue exitosa
if (!$result) {
    echo json_encode(["error" => "Error en la consulta: " . $mysqli->error]);
    exit();
}

// Inicializar el array para almacenar los posts
$posts = [];

// Recoger los resultados de la consulta en un array
while ($row = $result->fetch_assoc()) {
    $posts[] = $row;
}

// Devolver el resultado como JSON
echo json_encode($posts);

// Cerrar la conexión
$mysqli->close();
?>
