package game.creature;


public enum CreatureType {
	Player,
	Zombie,
	Turret,
	TurretZombie,
	ZombieTurret;
	
	public Creature instantiate() {
		switch(this) {
		case Player:
			return new Player();
		case Zombie:
			return new Zombie();
		case Turret:
			return new Turret();
		case TurretZombie:
			return new TurretZombie();
		case ZombieTurret:
			return new ZombieTurret();
		}
		return null;
	}
	
	public int getCode() {
		switch(this) {
		case Player:
			return 0;
		case Zombie:
			return 1;
		case Turret:
			return 2;
		case TurretZombie:
			return 3;
		case ZombieTurret:
			return 4;
		}
		return -1;
	}
	
	public static CreatureType fromCode(int code) {
		switch(code) {
		case 0:
			return Player;
		case 1:
			return Zombie;
		case 2:
			return Turret;
		case 3:
			return TurretZombie;
		case 4:
			return ZombieTurret;
		}
		return null;
	}
}
