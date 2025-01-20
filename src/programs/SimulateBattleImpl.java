package programs;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        while (hasAliveUnits(playerUnits) && hasAliveUnits(computerUnits)) {
            sortUnitsByAttack(playerUnits);
            sortUnitsByAttack(computerUnits);

            simulateRound(playerUnits, computerUnits);

            removeDeadUnits(playerUnits);
            removeDeadUnits(computerUnits);
        }

        if (hasAliveUnits(playerUnits)) {
            System.out.println("Player wins!");
        } else if (hasAliveUnits(computerUnits)) {
            System.out.println("Computer wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }

    private boolean hasAliveUnits(List<Unit> units) {
        return units.stream().anyMatch(Unit::isAlive);
    }

    private void sortUnitsByAttack(List<Unit> units) {
        units.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));
    }

    private void simulateRound(List<Unit> playerUnits, List<Unit> computerUnits) throws InterruptedException {
        Queue<Unit> allUnits = new LinkedList<>();

        allUnits.addAll(playerUnits);
        allUnits.addAll(computerUnits);

        while (!allUnits.isEmpty()) {
            Unit currentUnit = allUnits.poll();

            if (!currentUnit.isAlive()) continue;

            Unit target = currentUnit.getProgram().attack();

            if (target != null && target.isAlive()) {
                attack(currentUnit, target);

                printBattleLog.printBattleLog(currentUnit, target);
            }
        }
    }

    private void attack(Unit attacker, Unit target) {
        int damage = attacker.getBaseAttack();
        target.setHealth(target.getHealth() - damage);
        if (target.getHealth() <= 0) {
            target.setAlive(false);
        }
    }

    private void removeDeadUnits(List<Unit> units) {
        units.removeIf(unit -> !unit.isAlive());
    }
}