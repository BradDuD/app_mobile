<?php
include 'db.php';

header('Content-Type: application/json');

if (isset($_GET['post_id'])) {
    $post_id = $_GET['post_id'];
    $query = "SELECT comentarios.cuerpo_comentario, usuarios.nickname AS autor 
              FROM comentarios 
              JOIN usuarios ON comentarios.usuario_id = usuarios.id 
              WHERE comentarios.post_id = ?";

    $stmt = $conn->prepare($query);
    if ($stmt) {
        $stmt->bind_param("i", $post_id);
        $stmt->execute();
        $result = $stmt->get_result();

        $comments = array();
        while ($row = $result->fetch_assoc()) {
            $comments[] = $row;
        }

        echo json_encode($comments);
    } else {
        echo json_encode(["error" => "Error en la preparación de la consulta"]);
    }
} else {
    echo json_encode(["error" => "post_id no definido"]);
}
?>
