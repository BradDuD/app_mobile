<?php
include 'db.php';

if (isset($_GET['username'])) {
    $username = $_GET['username'];

    // Obtener el user_id del usuario con el nombre especificado
    $query = "SELECT id FROM usuarios WHERE nickname = ?";
    $stmt = $conn->prepare($query);
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $user = $result->fetch_assoc();
        $user_id = $user['id'];

        // Obtener los posts del usuario con la estructura completa y conteo de likes
        $posts_query = "
            SELECT p.id, p.titulo, p.contenido, p.fecha_publicacion, c.nombre_categoria AS categoria, 
                   u.nickname AS autor,
                   (SELECT COUNT(*) FROM likes WHERE post_id = p.id) AS like_count 
            FROM posts p 
            JOIN categorias c ON p.categoria_id = c.id
            JOIN usuarios u ON p.usuario_id = u.id
            WHERE p.usuario_id = ?
            ORDER BY p.fecha_publicacion DESC";
        
        $stmt = $conn->prepare($posts_query);
        $stmt->bind_param("i", $user_id);
        $stmt->execute();
        $posts_result = $stmt->get_result();

        $posts = [];
        while ($post = $posts_result->fetch_assoc()) {
            $posts[] = $post;
        }

        // Devolver el resultado como JSON
        echo json_encode($posts);
    } else {
        echo json_encode([]);
    }
    $stmt->close();
}

$conn->close();
?>
