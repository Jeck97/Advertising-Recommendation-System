<?php

require __DIR__.'/vendor/autoload.php';

use Kreait\Firebase\Factory;
use Kreait\Firebase\ServiceAccount;
$factory = (new Factory)->withServiceAccount(__DIR__. '/advertising-recommend-sy-85005-firebase-adminsdk-uberi-a6510f5dd9.json');
$database = $factory->createDatabase();

?>