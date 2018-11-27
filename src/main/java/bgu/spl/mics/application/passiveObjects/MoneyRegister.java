package bgu.spl.mics.application.passiveObjects;


import Accessories.FilePrinter;

import java.util.ArrayList;
import java.util.List;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister {

	private static MoneyRegister _moneyRegister;

	private List<OrderReceipt> _ordersList;

	/**
     * Retrieves the single instance of this class.
     */
	public static MoneyRegister getInstance() {
		if (_moneyRegister == null)
            _moneyRegister = new MoneyRegister();
		return _moneyRegister;
	}

	private MoneyRegister()
    {
        _ordersList = new ArrayList<>();
    }

	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		_ordersList.add(r);
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		int total = 0;
		for (OrderReceipt order : _ordersList)
		    total += order.getPrice();
		return total;
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
	    int amountLeft = c.getAvailableCreditAmount();
	    // TODO : amount left check is here ?
//	    if (amountLeft < amount)
//	        return;
	    c.charge(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
        FilePrinter.printToFile(_ordersList,filename);
	}
}
