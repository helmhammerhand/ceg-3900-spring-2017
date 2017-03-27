<!DOCTYPE html>
<!--
A simple PHP server to query a database for the xkcd tagger.
Results are returned between a pair of curly braces inside of HTML so they can
be viewed from a browser and easily parsed by the client android app.
-->
<html>
    <head>
        <meta charset="UTF-8">
        <title></title>
    </head>
    <body>
        <?php
			//database info (username and password removed for security purposes)
            $servername = "ceg3900xkcdtagger.cfknpyhefsoa.us-west-2.rds.amazonaws.com";	//database host name
            $username = "USERNAME"; //XXX place database username here
            $password = "PASSWORD";//XXX place database password here

            // Create connection
            $conn = new mysqli($servername, $username, $password);

            // Check connection
            if ($conn->connect_error) {
             die("Connection failed: " . $conn->connect_error);
            }
            echo "Connected successfully<br>"; 
				
			$sql = "USE xkcdtags" ;
			if ($conn->query($sql) === TRUE) {
				echo "Selected database successfully<br>";
			} else {
				echo "Error selecting database: " . $conn->error;
			}
		    
		    //GET parameters
		    //  ?type=tag&comic="name"&tag="tag"   //tag comic
		    //  ?type=untag&comic="name"&tag="tag" //untag comic
		    //  ?type=taglist   //list all tags
		    //  ?type=comictags&comic="comic"   //list all tags for a comic
		    //  ?type=searchtag&tag="tag"   //list comics with tag
		    //  
			switch ($_GET["type"]) {
			
			case "tag":
				//  ?type=tag&comic="name"&tag="tag" //tag comic
				tag($conn);				
				comictags($conn);
				break;

			case "untag":
				//  ?type=untag&comic="name"&tag="tag" //untag comic
				untag($conn);
				comictags($conn);
				break;
			case "taglist":
				//	?type=taglist 	//list all tags in the database
				taglist($conn);
				break;
			case "comictags":
				//  ?type=comictags   //list tags for comic
				comictags($conn);
				break;
			case "searchtag":
				//  ?type=searchtag&tag="tag"   //list comics with tag
				searchtag($conn);
				break;

			default:
				echo "{ERROR type " . $_GET["type"] . " not recognized}";
			}
	 		//end switch

			$conn->close();
			
			//////////////////////////////
			// database query functions //
			//////////////////////////////
			function tag($conn)
			{
				//add comic to Comics if needed
				$result = $conn->query("SELECT DISTINCT id FROM Comics 
					WHERE name = " . $conn->escape_string($_GET["comic"]));
				
				if($result->num_rows == 0)
				{
					//comic does not exist
					$result = $conn->query("INSERT INTO Comics (name) 
						VALUE (" . $conn->escape_string($_GET["comic"]) . ")");
				}
				//get comic id
				$result = $conn->query("SELECT DISTINCT id FROM Comics 
						WHERE name = " . $conn->escape_string($_GET["comic"]));
				$cid = $result->fetch_row()[0];

				//add tag to Tags if needed
				$result = $conn->query("SELECT DISTINCT `tag_id` FROM `Tags` 
						WHERE `name` = '" . $conn->escape_string($_GET["tag"]) . "'");

				if($result->num_rows == 0)
				{
					//tag does not exist
					$result = $conn->query("INSERT INTO `Tags` (name) 
						VALUE ('" . $conn->escape_string($_GET["tag"]) . "')");
				}
				//get comic id
				$result = $conn->query("SELECT DISTINCT `tag_id` FROM `Tags` 
						WHERE `name` = '" . $conn->escape_string($_GET["tag"]) . "'");
				$tid = $result->fetch_row()[0];

				//associate comic and tag by id
				$result = $conn->query("INSERT INTO `TagMap` (comic_id, tag_id) 
							VALUES ('" . $conn->escape_string($cid) . "', '" . $conn->escape_string($tid) . "')");

			}
			function untag($conn)
			{
				//find id of comic
				$result = $conn->query("SELECT DISTINCT id FROM Comics 
					WHERE name = " . $conn->escape_string($_GET["comic"]));
				if($result->num_rows == 0)
				{
					echo "{Failure: Comic does not exist}";
				}
				$cid = $result->fetch_row()[0];

				//find id of tag
				$result = $conn->query("SELECT DISTINCT `tag_id` FROM `Tags` 
					WHERE `name` = '" . $conn->escape_string($_GET["tag"]) . "'");
				if($result->num_rows == 0)
				{
					echo "{Failure: Tag does not exist}";
				}
				//get comic id
				$tid = $result->fetch_row()[0];
				//remove (comic_id, tag_id) from TagMap
				//DELETE FROM TagMap
				//	WHERE comic_id = "$cid"
				//	AND tag_id = "$tid"
				$result = $conn->query("DELETE FROM TagMap 
					WHERE comic_id = '" . $conn->escape_string($cid) . "'
					AND tag_id = '" . $conn->escape_string($tid) . "'");
			}
			function taglist($conn)
			{
				//get all tags in the database
				$result = $conn->query("SELECT `name` FROM `Tags`");
				echo "{";
				while($row = $result->fetch_row()) {
					print_r($row[0]);
					echo ",";
				}
				echo "}";
			}
			function comictags($conn)
			{
				//get names of tags associated with a comic
				$result = $conn->query("SELECT t.name FROM Tags t, Comics c, TagMap tm
					WHERE t.tag_id = tm.tag_id
					AND c.id = tm.comic_id
					AND c.name = '". $conn->escape_string($_GET["comic"]) . "'");
				//list names
				echo "{";
				while($row = $result->fetch_row()) {
					print_r($row[0]);
					echo ",";
				}
				echo "}";

			}
			function searchtag($conn)
			{
				//get names of comics associated with a tag
				$result = $conn->query("SELECT c.name FROM Tags t, Comics c, TagMap tm
					WHERE t.tag_id = tm.tag_id
					AND c.id = tm.comic_id
					AND t.name = '". $conn->escape_string($_GET["tag"]) . "'");
				//list names
				echo "{";
				while($row = $result->fetch_row()) {
					print_r($row[0]);
					echo ",";
				}
				echo "}";
			}

        ?>
    </body>
</html>


