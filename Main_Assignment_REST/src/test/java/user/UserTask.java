package user;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class UserTask {

    static PrintStream logDetailsForTask;

    {
        try {
            logDetailsForTask = new PrintStream(new File("src\\LogFileForUserTask"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<String> list;
    static Response response;

    // get the task from sheet
    @Test(priority = 7)
    public static void getTask() throws IOException {
        RestAssured.useRelaxedHTTPSValidation();
        String filePath = "src\\DataUser.xlsx";
        FileInputStream inputStream = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = null;
        XSSFCell cell = null;
        String taskFromExcel = null;
        String token=RegisterUser.tokens;
        System.out.println(token);
        list=new ArrayList<>();
        RequestSpecification request= given().
                baseUri("https://api-nodejs-todolist.herokuapp.com");
                request.header("Authorization","Bearer "+token).
                        header("Content-Type","application/json").
                        filter(RequestLoggingFilter.logRequestTo(logDetailsForTask)).
                        filter(ResponseLoggingFilter.logResponseTo(logDetailsForTask));
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                if (j == 0) {
                    taskFromExcel = cell.getStringCellValue();
                    JSONObject object = new JSONObject();
                    object.put("description", taskFromExcel);
                    request.body(object.toString()).post("/task");
                    list.add(taskFromExcel);
                    //System.out.println(taskFromExcel+" ");
                }
            }
        }
        response=request.get("/task");
       // System.out.println(response.prettyPrint());
    }

    // validate the added task
    @Test(priority = 8)
    public void validateTask(){
        System.out.println("validation of tasks");
        System.out.println(response.prettyPrint());
        String owner_id=RegisterUser.user_id;
        System.out.println(owner_id);
        JSONObject object=new JSONObject(response.asString());
        JSONArray arr=object.getJSONArray("data");
        for(int i=0;i<20;i++){
            String task= String.valueOf(arr.getJSONObject(i).get("description"));
            String owner= String.valueOf(arr.getJSONObject(i).get("owner"));
            Assert.assertEquals(task,list.get(i));
            Assert.assertEquals(owner,owner_id);
        }
        System.out.println("All the inputs are validated and it belongs to the user");
    }
}