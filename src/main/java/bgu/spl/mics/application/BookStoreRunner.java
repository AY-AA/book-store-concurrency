package bgu.spl.mics.application;


import Accessories.FileToString;
import Accessories.JSONParser;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import org.json.JSONArray;
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

        JSONObject jsonObject = new JSONObject(jsonString);
        BookInventoryInfo[] books = parseBooks(jsonObject);
        DeliveryVehicle[] vehicles = parseResources(jsonObject);

    }

    private BookInventoryInfo[] parseBooks(JSONObject jsonObject) {

        String[] types = {"bookTitle", "amount", "price"};

        ArrayList<Vector<String>> booksStrings = JSONParser.ParseJSON(jsonObject,"initialInventory",types);

        BookInventoryInfo[] books = new BookInventoryInfo[booksStrings.size()];
        int index = 0;

        for (Vector<String> currVector : booksStrings) {
            String bookName = currVector.elementAt(0);
            int bookAmount = Integer.parseInt(currVector.elementAt(1));
            int bookPrice = Integer.parseInt(currVector.elementAt(2));
            books[index] = new BookInventoryInfo(bookName,bookAmount,bookPrice);
            index ++;
        }
        return books;

    }

    private DeliveryVehicle[] parseResources(JSONObject jsonObject) {

        JSONArray jsonArray = jsonObject.optJSONArray("initialResources");
        JSONObject resourcesJSON = jsonArray.optJSONObject(0);

        String[] types = {"license", "speed"};

        ArrayList<Vector<String>> vehiclesStrings = JSONParser.ParseJSON(resourcesJSON,"vehicles",types);

        DeliveryVehicle[] vehicles = new DeliveryVehicle[vehiclesStrings.size()];
        int index = 0;

        for (Vector<String> currVector : vehiclesStrings) {
            int license = Integer.parseInt(currVector.elementAt(0));
            int speed = Integer.parseInt(currVector.elementAt(1));
            vehicles[index] = new DeliveryVehicle(license,speed);
            index ++;
        }
        return vehicles;

    }



    public static void main(String[] args) {

        String inputFile = System.getProperty("user.dir")+"/src/input.json";

        BookStoreRunner bookStore = new BookStoreRunner(inputFile);

    }
}
