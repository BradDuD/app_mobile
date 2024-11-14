<?php

if($_SERVER['REQUEST_METHOD']=='POST'){

    require_once ("db.php");

    $id_user =$_POST['id_user'];
    $nickname =$_POST['nickname'];
    $password =$_POST['password'];
    $email =$_POST['email'];

    $query = "UPDATE usuario SET nickname ='$nickname', password = '$password', email = '$email' WHERE id_user = '$id_user'";
    $result = $mysql->query($query);

    if ($mysql->affected_rows > 0){
        if ($result === TRUE){

            echo "User info updated succsessfully";
        }else{
            echo "Error";
        }
    }else{
        echo "not found rows";
    }

    $mysql->close();
}