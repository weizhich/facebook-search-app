<?php  
$mytoken = 'EAADqRasgqZA8BAIuYbmZCWe8xpph6orXmlrr5bQN0Ms9pa0VSLASdDcj02Lmwzh4MtXRyPOGnEyvGj8VWhhEUqR1lAJhhFZC3TIDjKF6We08PlmEMco8MsV51vZB9L5OZAnSC7tbYxn2B2DZBulPNx7MGN6kBhxQkxeGzeI8GZBtAZDZD';
//If the request is search
if(isset($_GET['value'])){
    $value = rawurlencode($_GET['value']);
    if (isset($_GET['latitude']))
        $latitude = $_GET['latitude'];
    else
        $latitude = 34.0193708;
    if (isset($_GET['longitude']))
        $longitude = $_GET['longitude'];
    else
        $longitude = -118.2889261;
    //search for user
    $rawdata = file_get_contents("https://graph.facebook.com/v2.8/search?q=$value&type=user&fields=id,name,picture.width(700).height(700)&limit=10&access_token=$mytoken");
    $user = json_decode($rawdata, true);
    //search for page
    $rawdata = file_get_contents("https://graph.facebook.com/v2.8/search?q=$value&type=page&fields=id,name,picture.width(700).height(700)&limit=10&access_token=$mytoken");
    $page = json_decode($rawdata, true);
    //search for event
    $rawdata = file_get_contents("https://graph.facebook.com/v2.8/search?q=$value&type=event&fields=id,name,picture.width(700).height(700)&limit=10&access_token=$mytoken");
    $event = json_decode($rawdata, true);
    //search for place
    $rawdata = file_get_contents("https://graph.facebook.com/v2.8/search?q=$value&type=place&fields=id,name,picture.width(700).height(700)&center=$latitude,$longitude&limit=10&access_token=$mytoken");
    $place = json_decode($rawdata, true);
    //search for group
    $rawdata = file_get_contents("https://graph.facebook.com/v2.8/search?q=$value&type=group&fields=id,name,picture.width(700).height(700)&limit=10&access_token=$mytoken");
    $group = json_decode($rawdata, true);
    //combine them together
    $arr = array('user' => $user, 'page' => $page, 'event' => $event, 'place' => $place, 'group' => $group);
}else if(isset($_GET['id'])){
    $id = $_GET['id'];
    $type = $_GET['type'];
    if (($type == 'Events') or ($type == 'events')){
        $rawdata = file_get_contents("https://graph.facebook.com/v2.8/$id?fields=id,name,picture.width(700).height(700),posts.limit(5)&access_token=$mytoken");
    }
    else{
        $rawdata = file_get_contents("https://graph.facebook.com/v2.8/$id?fields=id,name,picture.width(700).height(700),albums.limit(5){name,photos.limit(2){name,picture}},posts.limit(5)&access_token=$mytoken");
    }
    $arr = json_decode($rawdata, true);
}    

//return function  
$result = json_encode($arr);
$callback = $_GET['callback'];  
echo $callback."($result)"; 
?>