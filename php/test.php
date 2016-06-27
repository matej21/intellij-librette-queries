<?php
use App\Person;
use App\PersonQuery;
use Librette\Doctrine\Queries\EntityQuery;

function (\Librette\Queries\IQueryHandler $queryHandler)
{
	$result = $queryHandler->fetch(new PersonQuery());
	$person = $queryHandler->fetch(new EntityQuery(Person::class, 1));
	$person->getName();

	foreach ($result as $person) {
		$person->getName();
	}
}
