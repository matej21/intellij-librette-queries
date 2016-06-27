<?php
namespace App;

use Librette\Doctrine\Queries\Queryable;
use Librette\Doctrine\Queries\QueryObject;
use Librette\Doctrine\Queries\ResultSet;
use Librette\Queries\IQueryable;


/**
 * @method Person[]|ResultSet fetch(IQueryable $queryable)
 */
class PersonQuery extends QueryObject
{
	
	protected function createQuery(Queryable $queryable)
	{

	}

}
