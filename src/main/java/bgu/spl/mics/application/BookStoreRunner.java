package bgu.spl.mics.application;


import Accessories.FileToString;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public BookStoreRunner(String path) {
        String jsonString = FileToString.readFile(path);
        parseJSON(jsonString);
    }


    private void parseJSON(String path) {
        BookInventoryInfo[] books = null;
        try{
            // the main JSON object
            JSONObject jsonObject = new JSONObject(path);

            //inventory creation
            JSONArray inventoryArray = jsonObject.optJSONArray("initialInventory");
            books = new BookInventoryInfo[inventoryArray.length()];

            for (int i = 0; i < inventoryArray.length(); i++) {
                JSONObject currBook = inventoryArray.getJSONObject(i);

                String bookName = currBook.optString("bookTitle");
                int bookAmount = Integer.parseInt(currBook.optString("amount"));
                int bookPrice = Integer.parseInt(currBook.optString("price"));

                BookInventoryInfo book = new BookInventoryInfo(bookName,bookAmount,bookPrice);
                books[i] = book;
            }
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < books.length ; i++) {
            System.out.println(books[i].getBookTitle() + " amount : " + books[i].getAmountInInventory() + " price : " + books[i].getPrice());
        }
    }
    
    public static void main(String[] args) {

        String inputFile = System.getProperty("user.dir")+"/src/input.json";

        BookStoreRunner bookStore = new BookStoreRunner(inputFile);

    }
}
