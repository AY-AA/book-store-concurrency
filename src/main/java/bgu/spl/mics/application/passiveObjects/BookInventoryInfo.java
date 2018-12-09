package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private final String _bookTitle;
	private int _amountInInventory;
	private final int _price;

    public BookInventoryInfo(String _bookTitle, int amountInInventory, int price) {
        this._bookTitle = _bookTitle;
        this._amountInInventory = amountInInventory;
		_price = price;
    }

    /**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String get_bookTitle()
	{
		return _bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int get_amountInInventory()
	{
			return _amountInInventory;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice()
	{
		return _price;
	}

	public void takeBook()
	{
		if (_amountInInventory != 0)
			_amountInInventory--;
	}

}
