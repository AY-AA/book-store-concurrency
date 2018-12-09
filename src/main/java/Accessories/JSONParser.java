package Accessories;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

public class JSONParser {

    public static ArrayList<Vector<String>> ParseJSON(JSONObject jsonObject, String parse) {
        ArrayList<Vector<String>> ans = new ArrayList<>();
        try {
            // find the required array
            JSONArray jsonArray = jsonObject.optJSONArray(parse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject currObject = jsonArray.getJSONObject(i);
                Vector<String> currVector = new Vector<>();

                String bookName = currObject.optString("bookTitle");
                String bookAmount = currObject.optString("amount");
                String bookPrice = currObject.optString("price");

                currVector.add(bookName);
                currVector.add(bookAmount);
                currVector.add(bookPrice);

                ans.add(currVector);
            }
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    return ans;
    }
}
