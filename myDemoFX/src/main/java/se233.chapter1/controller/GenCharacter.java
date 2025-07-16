package se233.chapter1.controller;

import java.util.Random;
import se233.chapter1.model.character.BasedCharacter;
import se233.chapter1.model.character.BattleMageCharacter;
import se233.chapter1.model.character.MagicalCharacter;
import se233.chapter1.model.character.PhysicalCharacter;

public class GenCharacter {
    public static BasedCharacter setUpCharacter() {
        BasedCharacter character;
        Random rand = new Random();
        int type = rand.nextInt(3) + 1;
        int basedDef = rand.nextInt(50) + 1;
        int basedRes = rand.nextInt(50) + 1;
        if (type == 1) {
            character = new MagicalCharacter("MagicChar1", "se233.chapter1/assets/wizard.png", basedDef, basedRes);
        } else if(type == 2){
            character = new PhysicalCharacter("PhysicalChar1", "se233.chapter1/assets/knight.png", basedDef, basedRes);
        } else {
            character = new BattleMageCharacter("BattleMageChar1", "se233.chapter1/assets/battlemage.png", basedDef, basedRes);
        }
        return character;
    }
}