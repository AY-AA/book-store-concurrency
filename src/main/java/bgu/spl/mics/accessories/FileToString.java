package bgu.spl.mics.accessories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileToString {

    public static String readFile(String filePath)
    {
        FileReader fileToString;
        try {
            fileToString = new FileReader(filePath);
        }
        catch(IOException ex) {
            System.out.println("error: unable to open file '" + filePath + "'");
            return null;
        }
        return readFile(fileToString,filePath);
    }

    private static String readFile(FileReader fileReader, String filePath) {

        ArrayList<String> content = new ArrayList<>();

        String currRow;
        try {
            BufferedReader _bufferedReader = new BufferedReader(fileReader);
            while((currRow = _bufferedReader.readLine()) != null)
                content.add(currRow);
            _bufferedReader.close();
        }
        catch(IOException ex) {
            System.out.println("error: unable to read file '"  + filePath + "'");
            return null;
        }
        return arrayToString(content);
    }

    private static String arrayToString(ArrayList<String> content) {
        String ans = "";
        for ( String currLine : content)
            ans = ans + '\n' + currLine;
        return ans;
    }
}
