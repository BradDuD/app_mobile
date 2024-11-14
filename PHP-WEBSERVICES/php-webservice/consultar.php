<?php

if($_SERVER ['REQUEST_METHOD'] == 'GET' ){

    require_once("db.php");
    $id_user =$_GET['id_user'];


    $query =  "SELECT * FROM  usuarios WHERE id_user = '$id_user'";
    $result = $mysql->query($query);

    if($mysql->affected_rows > 0){
        while($row = $result -> fetch_assoc()){
            $array = $row;
        }
        echo json_encode($array);
    }else{
        echo "No se encontro usuario";
    }
    $result->close();
    $mysql->close();
}
