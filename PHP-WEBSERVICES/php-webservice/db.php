
<?php
$conn = new mysqli("127.0.0.1", "root", "", "post_db");

if ($conn-> connect_error) {
    die("failed to connect". $conn-> connect_error);

}
else {
    // echo "db is connected";
}