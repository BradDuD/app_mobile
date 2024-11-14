//eliminar//

<?php

if ($_SERVER['REQUEST_METHOD']=='POST'){

    require_once ("db.php");
    $id_user = $_POST['id_user'];

    $query = "DELETE FROM usuario WHERE id_user = '$id_user'";
    $result = $mysql->query($query);

    if ($mysql->affected_rows > 0){

        if ($result === TRUE){
            echo "the user was removed succesfully";
        
        }
    }else{
        echo "not found any user";
    }

    $mysql->close();

}