<?php
namespace App;

use Doctrine\ORM\Mapping as ORM;
use Nette\Object;
use Kdyby\Doctrine\Entities\Attributes\Identifier;

/**
 * @ORM\Entity
 */
class Person extends Object
{
	use Identifier;


	/**
	 * @var string
	 * @ORM\Column(type="string")
	 */
	protected $name;


	/**
	 * @return string
	 */
	public function getName()
	{
		return $this->name;
	}

}
