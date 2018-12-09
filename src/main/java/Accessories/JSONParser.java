package Accessories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

public class JSONParser {

    public static Vector<String> ParseJSON(JSONObject jsonObject, String[] types) { //receiving Object
        Vector<String> ans  = new Vector<>();
        Object currObj = new Object();
        for (int i = 0; i < types.length; i++) {
            currObj = jsonObject.get(types[i]);
            ans.add(currObj.toString());
        }
        return ans;
    }

    public static ArrayList<Vector<String>> ParseJSON(JSONArray jsonArray, String[] types) { //receiving Array
        ArrayList<Vector<String>> ans  = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Vector<String> currVec = new Vector<>();
            JSONObject currObj = (JSONObject)jsonArray.get(i);
            for (int j = 0; j < currObj.length(); j++) {
                currVec = ParseJSON(currObj,types);
            }
            ans.add(currVec);
        }
        return ans;
    }

}
