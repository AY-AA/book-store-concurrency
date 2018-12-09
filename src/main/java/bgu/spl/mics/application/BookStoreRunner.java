package bgu.spl.mics.application;


import Accessories.FileToString;
import Accessories.JSONParser;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public BookStoreRunner(String path) {
        String jsonString = FileToString.readFile(path);
        parseJSON(jsonString);
    }


    private void parseJSON(String jsonString) {

        parseBooks(jsonString);


    }

    private void parseBooks(String jsonString) {

        JSONObject jsonObject = new JSONObject(jsonString);

        ArrayList<Vector<String>> booksStrings = JSONParser.ParseJSON(jsonObject,"initialInventory");

        BookInventoryInfo[] books = null;
        int index = 0;
        books = new BookInventoryInfo[booksStrings.size()];

        for (Vector<String> currVector : booksStrings) {
            String bookName = currVector.elementAt(0);
            int bookAmount = Integer.parseInt(currVector.elementAt(1));
            int bookPrice = Integer.parseInt(currVector.elementAt(2));
            books[index] = new BookInventoryInfo(bookName,bookAmount,bookPrice);
            index ++;
        }

        
    }
    
    public static void main(String[] args) {

        String inputFile = System.getProperty("user.dir")+"/src/input.json";

        BookStoreRunner bookStore = new BookStoreRunner(inputFile);

    }
}
