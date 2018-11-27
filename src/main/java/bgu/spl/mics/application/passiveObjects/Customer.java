package bgu.spl.mics.application.passiveObjects;

import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {

    private final String _name;
    private final int _id;
	private final int _distance;
    private final String _address;
    private List<OrderReceipt> _orders;
    private int _availableCreditAmount;
    private final int _creditCard;

    public Customer(String _name, int _id, int _distance, String _address, List<OrderReceipt> _orders, int _availableCreditAmount, int _creditCard)
    {
        this._name = _name;
        this._id = _id;
        this._distance = _distance;
        this._address = _address;
        this._orders = _orders;
        this._availableCreditAmount = _availableCreditAmount;
        this._creditCard = _creditCard;
    }

    /**
     * Retrieves the name of the customer.
     */
	public String getName()
    {
		return _name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId()
    {
		return _id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress()
    {
		return _address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance()
    {
		return _distance;
	}

	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return _orders;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return _availableCreditAmount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return _creditCard;
	}

    public void charge(int amount) {
	    _availableCreditAmount =- amount;
    }
}
