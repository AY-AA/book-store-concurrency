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

    private Vector<MicroService> _microServices;
    private HashMap<Integer,Customer> _customers;

    public BookStoreRunner(String[] paths) {
        _microServices = new Vector<>();
        _customers = new HashMap<>();
        String jsonString = FileToString.readFile(paths[0]);
        parseJSONAndLoad(jsonString);
        startThreads();

        printToFiles(paths);

        boolean print =  _microServices.size() == MicroService.x;
        System.out.println("--- ALL THREADS FINISHED  = " + print + " ---");

        System.exit(0);

    }

    private void printToFiles(String[] paths) {
        FilePrinter.printToFile(_customers,paths[1]);
        Inventory.getInstance().printInventoryToFile(paths[2]);
        MoneyRegister.getInstance().printOrderReceipts(paths[3]);
        MoneyRegister.getInstance().printObject(paths[4]);

    }

    private void startThreads() {
        MicroService timeService = _microServices.lastElement();
        for (MicroService microService : _microServices)
        {
            Thread thread = new Thread(microService);
            thread.setName(microService.getName());
            thread.start();
            if (microService == timeService) {
                try {
                    System.out.println("--- THREADS STARTED ---");
                    thread.join();
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
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

        int[] simpleServices = parseSimpleServices(jsonObject);
        loadServices(simpleServices);

        HashMap<Customer,HashMap<Integer,Vector<String>>> APIs = parseCustomers(jsonObject);
        loadAPIs(APIs);

        Vector<String> timeService = parseTime(jsonObject);
        int speed = Integer.parseInt(timeService.elementAt(0));
        int duration = Integer.parseInt(timeService.elementAt(1));
        _microServices.add(new TimeService(speed,duration));
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
     * This method parses books objects from JSON file
     * @param jsonObject JSON object holds an array of books inside it
     * @return books array parsed off the json
     */
    private BookInventoryInfo[] parseBooks(JSONObject jsonObject) {

        int index = 0;
        JSONArray booksJson = jsonObject.getJSONArray("initialInventory");
        String[] types = {"bookTitle", "amount", "price"};
        ArrayList<Vector<String>> books = JSONParser.ParseJSON(booksJson,types);
        BookInventoryInfo[] ans = new BookInventoryInfo[booksJson.length()];
        for(Vector<String> v : books){
            String title = v.get(0);
            int amount = Integer.parseInt(v.get(1));
            int price = Integer.parseInt(v.get(2));
            ans[index] = new BookInventoryInfo(title,amount,price);
            index++;
        }
        return ans;
    }

    /**
     * This method parses vehicles objects from JSON file
     * @param jsonObject JSON object holds an array of vehicles inside it
     * @return
     */
    private DeliveryVehicle[] parseResources(JSONObject jsonObject) {

        JSONArray jsonArray = jsonObject.optJSONArray("initialResources");
        JSONObject vehiclesJson = jsonArray.optJSONObject(0);
        JSONArray vehiclesAsArray = vehiclesJson.optJSONArray("vehicles");

        String[] types = {"license", "speed"};

        ArrayList<Vector<String>> vehiclesStrings = JSONParser.ParseJSON(vehiclesAsArray,types);

        DeliveryVehicle[] vehicles = new DeliveryVehicle[vehiclesAsArray.length()];
        int index = 0;

        for (Vector<String> currVector : vehiclesStrings) {
            int license = Integer.parseInt(currVector.elementAt(0));
            int speed = Integer.parseInt(currVector.elementAt(1));
            vehicles[index] = new DeliveryVehicle(license, speed);
            index++;
        }
        return vehicles;
    }

    private Vector<String> parseTime(JSONObject jsonObject) {
        JSONObject services = jsonObject.optJSONObject("services");
        JSONObject timeJson = services.optJSONObject("time");
        String[] types = {"speed", "duration"};
        Vector<String> time = JSONParser.ParseJSON(timeJson, types);
        return time;
    }

    private int[] parseSimpleServices(JSONObject jsonObject) {
        int[] simpleServices = new int[4];
        JSONObject services = jsonObject.optJSONObject("services");
        int selling = Integer.parseInt(services.optString("selling"));
        int inventoryService = Integer.parseInt(services.optString("inventoryService"));
        int logistics = Integer.parseInt(services.optString("logistics"));
        int resourcesService = Integer.parseInt(services.optString("resourcesService"));

        simpleServices[0] = selling;
        simpleServices[1] = inventoryService;
        simpleServices[2] = logistics;
        simpleServices[3] = resourcesService;

        return simpleServices;
    }


    private HashMap<Customer, HashMap<Integer, Vector<String>>> parseCustomers(JSONObject jsonObject) {
        HashMap<Customer, HashMap<Integer, Vector<String>>> ans = new HashMap<>();
        JSONObject services = jsonObject.optJSONObject("services");
        JSONArray customersJson = services.optJSONArray("customers");
        String[] types = {"id", "name", "address", "distance"};
        ArrayList<Vector<String>> customersSimpleData = JSONParser.ParseJSON(customersJson, types);
        JSONObject currCredit;
        Vector<String> currCreditVec;

        String[] types1 = {"number","amount"};
        String[] types2 = {"bookTitle","tick"};

        for (int i = 0; i < customersJson.length(); i++) {
            currCredit = (JSONObject)((JSONObject)customersJson.get(i)).get("creditCard");
            JSONArray currOrderSchedule = (JSONArray)((JSONObject)customersJson.get(i)).get("orderSchedule");
            currCreditVec = JSONParser.ParseJSON(currCredit,types1);
            ArrayList<Vector<String>> orderScheduleAns = JSONParser.ParseJSON(currOrderSchedule,types2);

            createDataForAPI(ans, customersSimpleData, currCreditVec, i, orderScheduleAns);
        }
        return ans;
    }

    private void createDataForAPI(HashMap<Customer, HashMap<Integer, Vector<String>>> ans,
                                  ArrayList<Vector<String>> customersSimpleData, Vector<String> currCreditVec,
                                  int i, ArrayList<Vector<String>> orderScheduleAns) {
        int id = Integer.parseInt(customersSimpleData.get(i).get(0));
        String name = customersSimpleData.get(i).get(1);
        String address = customersSimpleData.get(i).get(2);
        int distance = Integer.parseInt(customersSimpleData.get(i).get(3));

        int number = Integer.parseInt(currCreditVec.get(0));
        int amount = Integer.parseInt(currCreditVec.get(1));

        HashMap<Integer,Vector<String>> orders = new HashMap<>();
        for (int j = 0; j < orderScheduleAns.size(); j++) {
            String title = orderScheduleAns.get(j).get(0);
            int tick = Integer.parseInt(orderScheduleAns.get(j).get(1));
            if(!orders.containsKey(tick)){
                orders.put(tick,new Vector<>());
            }
            orders.get(tick).add(title);
        }

        Customer currCustomer = new Customer(name,id,distance,address,amount,number);
        _customers.put(id,currCustomer);
        ans.put(currCustomer,orders);
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
     * args = {
     *      input : json path,
     *      output : customers path, output : books path, output : orders path , output : money register path
     * }
     * @param args
     */
    public static void main(String[] args) {
//        BookStoreRunner bookStore = new BookStoreRunner(args[0]);

        // args[1] = customersPath

        System.out.println("--- PROGRAM STARTED ---");

        String inputFile = System.getProperty("user.dir");
        String[] a = {inputFile + "/src/input.json",inputFile + "/customers.txt",inputFile + "/books.txt",inputFile + "/orders.txt",inputFile + "/moneyRegister.txt"};
        BookStoreRunner bookStore = new BookStoreRunner(a);

//        BookStoreRunner bookStore = new BookStoreRunner(args);


    }
}
