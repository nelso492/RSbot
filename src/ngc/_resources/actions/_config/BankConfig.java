package ngc._resources.actions._config;

import org.powerbot.script.Area;
import org.powerbot.script.Tile;

public class BankConfig {
    private int primaryDepositId;
    private int secondaryDepositId;

    private int primaryWithdrawId;
    private int primaryWithdrawQty;

    private int secondaryWithdrawId;
    private int secondaryWithdrawQty;

    private boolean bankOnInventoryFull; // Bank on full inventory
    private boolean bankOnWithdrawsEmpty; // Bank when withdrawn items empty (i.e. smelting or crafting one item into another)
    private boolean closeWhenDone;

    private Area bankArea;
    private Tile[] toBankPath;

    /**
     * Establish bank interaction parameters.
     * Setting primaryDepositId to 0 will deposit all inventory
     * @param primaryDepositId First item to deposit
     * @param secondaryDepositId Second itme to deposit
     * @param primaryWithdrawId First item to withdraw
     * @param primaryWithdrawQty First item QTY to withdraw
     * @param secondaryWithdrawId Second item to withdraw
     * @param secondaryWithdrawQty Second item QTY to withdraw
     * @param bankOnInventoryFull Trigger banking on inventory FULL
     * @param bankOnWithdrawsEmpty Trigger banking on inventory EMPTY
     * @param closeWhenDone Manually close the bank window on completion
     */
    public BankConfig(int primaryDepositId, int secondaryDepositId, int primaryWithdrawId, int primaryWithdrawQty, int secondaryWithdrawId, int secondaryWithdrawQty, boolean bankOnInventoryFull, boolean bankOnWithdrawsEmpty, boolean closeWhenDone) {
        this.primaryDepositId = primaryDepositId;
        this.secondaryDepositId = secondaryDepositId;
        this.primaryWithdrawId = primaryWithdrawId;
        this.primaryWithdrawQty = primaryWithdrawQty;
        this.secondaryWithdrawId = secondaryWithdrawId;
        this.secondaryWithdrawQty = secondaryWithdrawQty;
        this.bankOnInventoryFull = bankOnInventoryFull;
        this.bankOnWithdrawsEmpty = bankOnWithdrawsEmpty;
        this.closeWhenDone = closeWhenDone;
    }

    public int getPrimaryDepositId() {
        return primaryDepositId;
    }

    public int getSecondaryDepositId() {
        return secondaryDepositId;
    }

    public int getPrimaryWithdrawId() {
        return primaryWithdrawId;
    }

    public int getPrimaryWithdrawQty() {
        return primaryWithdrawQty;
    }

    public int getSecondaryWithdrawId() {
        return secondaryWithdrawId;
    }

    public int getSecondaryWithdrawQty() {
        return secondaryWithdrawQty;
    }

    public boolean isBankOnInventoryFull() {
        return bankOnInventoryFull;
    }

    public boolean isBankOnWithdrawsEmpty() {
        return bankOnWithdrawsEmpty;
    }

    public boolean isCloseWhenDone() {
        return closeWhenDone;
    }

    public int[] getAllDepositIds() {
        int[] ids = {primaryDepositId, secondaryDepositId};
        return ids;
    }

    public int[] getAllWithdrawIds() {
        int[] ids = {primaryWithdrawId, secondaryWithdrawId};
        return ids;
    }

    public Area getBankArea() {
        return bankArea;
    }

    public void setBankArea(Area bankArea) {
        this.bankArea = bankArea;
    }

    public Tile[] getToBankPath() {
        return toBankPath;
    }

    public void setToBankPath(Tile[] toBankPath) {
        this.toBankPath = toBankPath;
    }
}
