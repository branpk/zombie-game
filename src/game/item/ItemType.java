package game.item;

import game.Vector;

public enum ItemType {
	Key,
	HealthPack,
	AmmoPack;
	
	public Item instantiate(Vector pos) {
		switch (this) {
		case Key:
			return new Key(pos);
		case HealthPack:
			return new HealthPack(pos);
		case AmmoPack:
			return new AmmoPack(pos);
		}
		return null;
	}
	
	public int getCode() {
		switch (this) {
		case Key:
			return 0;
		case HealthPack:
			return 1;
		case AmmoPack:
			return 2;
		}
		return -1;
	}
	
	public static ItemType fromCode(int code) {
		switch (code) {
		case 0:
			return Key;
		case 1:
			return HealthPack;
		case 2:
			return AmmoPack;
		}
		return null;
	}
}
