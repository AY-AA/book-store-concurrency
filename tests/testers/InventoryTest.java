package testers;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class InventoryTest {

    Inventory testInventory;
    private BookInventoryInfo[] bookInventoryInfos;
    private OrderResult res;
    private int isAvailable;

    @Before
    public void testSetUp() {
        testInventory = Inventory.getInstance();
        bookInventoryInfos = giveMeBooks();
        testInventory.load(bookInventoryInfos);
        isAvailable = -2;
    }

    @Test
    public void testGetInstance() {
        Assert.assertNotNull(testInventory); //Checks if the singleton design pattern has been implemented correctly.
    }

    //------- load -------
    @Test
    public void testLoad() {
        //checking if the items were loaded to inventory
        for (int i = 0; i < bookInventoryInfos.length; i++) {
            res = testInventory.take(bookInventoryInfos[i].getBookTitle());
            Assert.assertEquals(res,OrderResult.SUCCESSFULLY_TAKEN);
        }
    }

    //------- take -------
    @Test
    public void testTake_decreasingAmount() {
        //If amount of taken book decreasing (from one to zero) after action.
        res = testInventory.take(bookInventoryInfos[2].getBookTitle());
        isAvailable = testInventory.checkAvailabiltyAndGetPrice(bookInventoryInfos[2].getBookTitle());
        Assert.assertEquals(-1, isAvailable);
    }

    @Test
    public void testTake_successfullyTaken() {
        res = testInventory.take(bookInventoryInfos[1].getBookTitle());
        Assert.assertEquals(res, OrderResult.SUCCESSFULLY_TAKEN);
    }

    @Test
    public void testTake_nonExisting() {
        //If tried to take non existing book
        res = testInventory.take("nonExistingBook");
        Assert.assertEquals(OrderResult.NOT_IN_STOCK,res);
    }

    //------- checkAvailabilityAndGetPrice -------
    @Test
    public void testCheckAvailabilityAndGetPrice_ExistingBook() {
        //checking if an existing book is available and received the same price
        isAvailable = testInventory.checkAvailabiltyAndGetPrice(bookInventoryInfos[2].getBookTitle());
        Assert.assertEquals(isAvailable,bookInventoryInfos[2].getPrice());
    }

    @Test
    public void testCheckAvailabilityAndGetPrice_NonExistingBook() {
        //checking if a non existing book is available
        isAvailable = testInventory.checkAvailabiltyAndGetPrice("nonExistingBook");
        Assert.assertEquals(-1, isAvailable);
    }

    //------- printInventoryToFile -------
    @Test
    public void printInventoryToFile() {
        String file = "books.txt";
        testInventory.printInventoryToFile(file);
        HashMap<String,Integer> books = null;

        // Deserialization
        try
        {
            // Reading the object from a file
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileInputStream);

            // Method for deserialization of object
            books = (HashMap<String,Integer>)in.readObject();

            in.close();
            fileInputStream.close();
        }

        catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }

        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }

        //after deserialization
        //Todo: get the books by getter from inventory and compare to books (deserialized object)
    }

    private BookInventoryInfo[] giveMeBooks(){
        BookInventoryInfo[] bookInventoryInfos= new BookInventoryInfo[3];
        bookInventoryInfos[0] = new BookInventoryInfo("HarryPotterAndTheChamberOfSecrets",2,80);
        bookInventoryInfos[1] = new BookInventoryInfo("50ShadesOfGray",3,99);
        bookInventoryInfos[2] = new BookInventoryInfo("midSummerNightDreams",1,105);
        return bookInventoryInfos;
    }
}