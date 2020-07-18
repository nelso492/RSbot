package ngc._resources.actions;

import ngc._resources.actions._config.BankConfig;
import ngc._resources.actions._template.BaseAction;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

import static org.powerbot.script.Condition.sleep;

public class BankAction extends BaseAction<ClientContext> {
    private BankConfig config;

    public BankAction(ClientContext ctx, String status, BankConfig _config) {
        super(ctx, status);
        config = _config;
    }

    @Override
    public boolean activate() {
        boolean invFull = (config.isBankOnInventoryFull() && ctx.inventory.isFull());
        boolean resourcesEmpty = (config.isBankOnWithdrawsEmpty() && ctx.inventory.select().id(config.getAllWithdrawIds()).count() == 0);
        return (invFull || resourcesEmpty) && ctx.bank.inViewport();
    }


    @Override
    public void execute() {
        if (ctx.bank.inViewport()) {

            if (!ctx.bank.opened()) {
                // Open Bank
                ctx.bank.open();
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.opened();
                    }
                }, 150, 20);
            }

            // Deposit if needed
            if (config.getPrimaryDepositId() > -1) {
                if (config.getPrimaryDepositId() == 0) {
                    ctx.bank.depositInventory();
                } else {
                    ctx.bank.deposit(config.getPrimaryDepositId(), Bank.Amount.ALL);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(config.getPrimaryDepositId()).count() == 0;
                        }
                    }, 150, 100);
                    ctx.bank.deposit(config.getSecondaryDepositId(), Bank.Amount.ALL);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(config.getSecondaryDepositId()).count() == 0;
                        }
                    }, 150, 100);
                }
            }

            // Withdraw
            if (config.getPrimaryWithdrawId() > 0) {

                // Check Quantity
                if (config.getPrimaryWithdrawQty() == 28) {
                    ctx.bank.withdraw(config.getPrimaryWithdrawId(), Bank.Amount.ALL);
                } else {
                    ctx.bank.withdraw(config.getPrimaryWithdrawId(), Bank.Amount.X);//.select().id(config.getPrimaryWithdrawId()).poll().click(); // Uses X qty

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(config.getPrimaryWithdrawId()).count() > 0;
                        }
                    }, 250, 50);

                    // Check secondary withdraw if primary quantity less than full inventory
                    if (config.getSecondaryWithdrawId() > 0) {
                        ctx.bank.withdraw(config.getSecondaryDepositId(), Bank.Amount.X);//.select().id(config.getSecondaryWithdrawId()).poll().click(); // Uses X qty

                        Condition.wait(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return ctx.inventory.select().id(config.getSecondaryWithdrawId()).count() > 0;
                            }
                        }, 150, 20);
                    }
                }
            }

            // Close if needed
            if (config.isCloseWhenDone() && ctx.bank.open()) {
                ctx.bank.close();
                sleep(Random.nextInt(400, 1200));
            }
        } else {
            if (this.config.getBankArea() != null) {
                ctx.movement.step(this.config.getBankArea().getRandomTile());
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.bank.inViewport();
                    }
                }, 350, 10);
            }
        }
    }

}
