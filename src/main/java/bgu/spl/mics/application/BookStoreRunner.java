package bgu.spl.mics.application;


import Accessories.FilePrinter;
import Accessories.FileToString;
import Accessories.JSONParser;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {

    Vector<MicroService> _microServices;


    public BookStoreRunner(String path) {
        _microServices = new Vector<>();
        String jsonString = FileToString.readFile(path);
        parseJSONAndLoad(jsonString);
    }

    /**
     * This method is responsible of parsing and loading information and instances from JSON file
     * @param jsonString is the JSON file converted into a String file
     */
    private void parseJSONAndLoad(String jsonString) {

        JSONObject jsonObject = new JSONObject(jsonString);

        // books parse and load
        BookInventoryInfo[] books = parseBooks(jsonObject);
        Inventory.getInstance().load(books);

        // vehicles parse and load
        DeliveryVehicle[] vehicles = parseResources(jsonObject);
        ResourcesHolder.getInstance().load(vehicles);



        loadServices(null);
        loadAPIs(null);
    }

    /**
     * This method loads micro services which are selling, inventory, logistics and resources micro services
     * numOfServices is an array which represents how much micro services we will have in the program
     * @param numOfServices indexes: 0- selling, 1- inventory, 2- logistics, 3- resources
     */
    private void loadServices(int [] numOfServices) {
        int services = numOfServices[0];
        for (int j = 0 ; j < services ; j++){
                _microServices.add(new SellingService("SellingService " + j));
        }
        services = numOfServices[1];
        for (int j = 0 ; j < services ; j++){
            _microServices.add(new InventoryService("InventorySerivce " + j));
        }
        services = numOfServices[2];
        for (int j = 0 ; j < services ; j++){
            _microServices.add(new LogisticsService("LogisticsSerivce " + j));
        }
        services = numOfServices[3];
        for (int j = 0 ; j < services ; j++){
            _microServices.add(new ResourceService("ResourcesSerivce " + j));
        }
    }

    /**
     * This method loads all APIServices
     * @param apis is a hashmap which holds as keys costumers and a hashmap as keys
     *             for each customer, the hash map represents tick and its book name to order at the same tick
     */
    private void loadAPIs(HashMap<Customer,HashMap<Integer,Vector<String>>> apis) {
        for (Customer customer : apis.keySet())
        {
            _microServices.add(new APIService(customer,apis.get(customer)));
        }
    }

    /**
     * This method parses books objects from JSON file
     * @param jsonObject JSON object holds an array of books inside it
     * @return books array parsed off the json
     */
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

    /**
     * This method parses vehicles objects from JSON file
     * @param jsonObject JSON object holds an array of vehicles inside it
     * @return
     */
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

    private void printObjects(String customersPath, String booksPath, String ordersPath, String moneyPath)
    {
        printCustomers(customersPath);
        Inventory.getInstance().printInventoryToFile(booksPath);
        MoneyRegister.getInstance().printOrderReceipts(ordersPath);
        MoneyRegister.getInstance().printObject(moneyPath);

    }

    private void printCustomers(String customersPath) {
        HashMap<Integer,Customer> customerHashMap = new HashMap<>();
        for (MicroService microService : _microServices) {
            if (microService instanceof APIService){
                Customer customer = ((APIService) microService).get_customer();
                customerHashMap.put(customer.getId(), customer);
            }
        }
        FilePrinter.printToFile(customerHashMap,customersPath);
    }

    /**
     * args = {
     *      input : json path,
     *      output : customers path, output : books path, output : orders path , output : money register path
     * }
     * @param args
     */
    public static void main(String[] args) {
//        BookStoreRunner bookStore = new BookStoreRunner(args[0]);

        // args[1] = customersPath

        String x = "a";
        String y = x.toString();
        System.out.println(y);

        String inputFile = System.getProperty("user.dir")+"/src/input.json";


        BookStoreRunner bookStore = new BookStoreRunner(inputFile);

    }
}
