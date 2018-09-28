<?php
$q = isset($_GET["q"]) ? intval($_GET["q"]) : '';
$con = mysqli_connect('localhost', 'root', 'lw14757473dlxl');
if (!$con) {
    die('Could not connect: ' . mysqli_error($con));
}
mysqli_select_db($con, "test");
mysqli_set_charset($con, "utf8");
$sql = "SELECT * FROM tt";
$result = mysqli_query($con, $sql);
echo "<table border='1'>
<tr>
<th>ID</th>
<th>NAME</th>
<th>AGE</th>
</tr>";
while ($row = mysqli_fetch_array($result)) {
    echo "<tr>";
    echo "<td>" . $row['id'] . "</td>";
    echo "<td>" . $row['name'] . "</td>";
    echo "<td>" . $row['age'] . "</td>";
    echo "</tr>";
}
echo "</table>";
mysqli_close($con);
<?php>
