package bgu.spl.mics.application;


import Accessories.FileToString;
import Accessories.JSONParser;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.services.TimeService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.HashMap;
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
        int[] simpleServices = parseSimpleServices(jsonObject);
        HashMap<Customer,HashMap<Integer,Vector<String>>> APIs = parseCustomers(jsonObject);
    }

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

    //Parse Resources
    private Vector<String> parseTime(JSONObject jsonObject) {
        JSONObject services = jsonObject.optJSONObject("services");
        JSONObject timeJson = services.optJSONObject("time");
        String[] types = {"speed", "duration"};
        Vector<String> time = JSONParser.ParseJSON(services, types);
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
        ans.put(currCustomer,orders);
    }


    public static void main(String[] args) {

        String inputFile = System.getProperty("user.dir")+"/src/input.json";

        BookStoreRunner bookStore = new BookStoreRunner(inputFile);

    }
}
