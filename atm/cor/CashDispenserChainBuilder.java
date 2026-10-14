package org.lld_yt.atm.cor;

public class CashDispenserChainBuilder {
    private CashDispenserChainBuilder() {}

    /** Builds 2000 -> 500 -> 100 and returns the head of the chain. Largest denomination first. */
    public static CashDispenser build() {
        CashDispenser twoThousand = new TwoThousandDispenser();
        CashDispenser fiveHundred = new FiveHundredDispenser();
        CashDispenser oneHundred = new OneHundredDispenser();

        twoThousand.setNextDispenser(fiveHundred);
        fiveHundred.setNextDispenser(oneHundred);
        return twoThousand;
    }
}