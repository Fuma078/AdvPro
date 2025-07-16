package se233.chapter1.controller;

import se233.chapter1.model.DamageType;
import se233.chapter1.model.item.BasedEquipment;
import se233.chapter1.model.item.Weapon;
import se233.chapter1.model.item.Armor;
import java.util.ArrayList;

public class GenItemList {
    public static ArrayList<BasedEquipment> setUpItemList() {
        ArrayList<BasedEquipment> itemLists = new ArrayList<BasedEquipment>(5);

        itemLists.add(new Weapon("Sword", 10, DamageType.physical, "se233.chapter1/assets/sword.png"));
        itemLists.add(new Weapon("Gun", 20, DamageType.physical, "se233.chapter1/assets/gun.png"));
        itemLists.add(new Weapon("Staff", 30, DamageType.magical, "se233.chapter1/assets/staff.png"));
        itemLists.add(new Armor("Shirt", 0, 50, "se233.chapter1/assets/shirt.png"));
        itemLists.add(new Armor("Armor", 50, 0, "se233.chapter1/assets/armor.png"));
        itemLists.add(new Weapon("MagicBook", 20, DamageType.magical, "se233.chapter1/assets/magicbook.png"));

        return itemLists;
    }
}