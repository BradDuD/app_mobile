<?php
// Incluir el archivo de conexión a la base de datos
include 'db.php';

// Comprobar si los parámetros necesarios han sido enviados
if (isset($_POST['post_id']) && isset($_POST['usuario_id'])) {
    $post_id = $_POST['post_id'];
    $usuario_id = $_POST['usuario_id'];

    // Verificar si el usuario ya ha dado like al post para evitar duplicados
    $check_like_query = "SELECT * FROM likes WHERE post_id = ? AND usuario_id = ?";
    $stmt = $conn->prepare($check_like_query);
    $stmt->bind_param("ii", $post_id, $usuario_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows == 0) {
        // Si no ha dado like aún, insertar un nuevo registro en la tabla de likes
        $like_query = "INSERT INTO likes (post_id, usuario_id) VALUES (?, ?)";
        $stmt = $conn->prepare($like_query);
        $stmt->bind_param("ii", $post_id, $usuario_id);

        if ($stmt->execute()) {
            // Contar los likes actuales
            $count_query = "SELECT COUNT(*) as like_count FROM likes WHERE post_id = ?";
            $stmt = $conn->prepare($count_query);
            $stmt->bind_param("i", $post_id);
            $stmt->execute();
            $result = $stmt->get_result();
            $row = $result->fetch_assoc();
            $new_like_count = $row['like_count'];

            // Devolver una respuesta exitosa con el nuevo contador de likes
            echo json_encode(array('status' => 'success', 'like_count' => $new_like_count));
        } else {
            // Error al insertar el like
            echo json_encode(array('status' => 'error', 'message' => 'Error al agregar like.'));
        }
    } else {
        // Si el usuario ya ha dado like, devolver un mensaje indicando que ya se hizo
        echo json_encode(array('status' => 'error', 'message' => 'Ya has dado like a este post.'));
    }

    $stmt->close();
} else {
    // Parámetros faltantes
    echo json_encode(array('status' => 'error', 'message' => 'Parámetros faltantes.'));
}

$conn->close();
?>
