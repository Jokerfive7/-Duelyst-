package utils;

import java.util.Map;

import structures.basic.Unit;
import structures.GameState;


public class CreateUnitHelper {
    public static void addAbilityToUnit(GameState gameState){
        // Set ability to all unit
        Map<Integer,Unit> tempUnitList = gameState.getallUnits();
        for(int i = 0; i < 40; i++){
            if(i == 1 || i == 13){
                tempUnitList.get(i).setSpellThief(true);
            }
            if(i == 5 || i == 15 || i == 23 || i == 33){
                tempUnitList.get(i).setOnSummon(true);
            }
            if(i == 3 || i == 10 || i == 20 || i == 30 || i == 6 || i == 16){
                tempUnitList.get(i).setProvoke(true);
            }
            if(i == 3 || i == 10){
                tempUnitList.get(i).setAnger(true);
            }
            if(i == 7 || i == 17 || i == 26 || i == 36){
                tempUnitList.get(i).setWindfury(true);
            }
            if(i == 2 || i == 11 || i == 25 || i == 35){
                tempUnitList.get(i).setRanged(true);
            }
            if(i == 6 || i == 16 || i == 28 || i == 38){
                tempUnitList.get(i).setAirdrop(true);
            }
            if(i == 24 || i == 34){
                tempUnitList.get(i).setFlying(true);
                tempUnitList.get(i).setOnDeath(true);
            }

        }
    }
}
