package user;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.*;

import static io.restassured.RestAssured.given;

public class RegisterUser {

    PrintStream logDetails;

    {
        try {
            logDetails = new PrintStream(new File("src\\LogFileForUser.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static String tokens;
    static String user_id;

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    Response responseLogin;
    Response responseRegister;


    String name,email,password;
    double age;

    // setup method is for reading data for sheet and for specBuilders.
    @BeforeClass
    public void setup() throws IOException {


        //request 1
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type", "application/json");
        requestSpecification = RestAssured.with().spec(requestSpecBuilder.build());

        //response 1
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON);
        responseSpecification = responseSpecBuilder.build();


        FileInputStream registerFile = new FileInputStream("C:\\Users\\gunass\\Main_Assignment_REST\\src\\RegisterData.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(registerFile);
        XSSFSheet sheet = workbook.getSheetAt(0);
        name = sheet.getRow(1).getCell(0).getStringCellValue();
        email = sheet.getRow(1).getCell(1).getStringCellValue();
        password = sheet.getRow(1).getCell(2).getStringCellValue();
        age = sheet.getRow(1).getCell(3).getNumericCellValue();

    }

    // User registration testCase
    @Test(priority = 1)
    public void  userRegister(){
        RestAssured.useRelaxedHTTPSValidation();
        JSONObject registerObject = new JSONObject();
        registerObject.put("name", name);
        registerObject.put("email", email);
        registerObject.put("password", password);
        registerObject.put("age", age);

        responseRegister = given().spec(requestSpecification).
                filter(RequestLoggingFilter.logRequestTo(logDetails)).
                filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                body(registerObject.toString()).
                when().post("/user/register").
                then().statusCode(201).extract().response();

    }

    // user  registration validation testCase
    @Test(priority = 2)
    public void userRegistrationValidate(){
        assertThat(responseRegister.path("user.email"), is(equalTo(email)));
        assertThat(responseRegister.path("user.name"), is(equalTo(name)));
        System.out.println("Validation successful");
    }

    // user registration negative testCase - already registered user
    @Test(priority = 3)
    public void  userRegisterNegative(){
        RestAssured.useRelaxedHTTPSValidation();
        JSONObject registerObject = new JSONObject();
        registerObject.put("name", name);
        registerObject.put("email", email);
        registerObject.put("password", password);
        registerObject.put("age", age);

        responseRegister = given().spec(requestSpecification).
                filter(RequestLoggingFilter.logRequestTo(logDetails)).
                filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                body(registerObject.toString()).
                when().post("/user/register").
                then().statusCode(400).extract().response();

    }

    // user login testCase
    @Test(priority = 4)
    public void userLogin(){
        RestAssured.useRelaxedHTTPSValidation();
        JSONObject loginObject = new JSONObject();
        loginObject.put("email", email);
        loginObject.put("password", password);

        responseLogin = given().spec(requestSpecification).
                filter(RequestLoggingFilter.logRequestTo(logDetails)).
                filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                body(loginObject.toString()).
                when().post("/user/login").
                then().statusCode(200).extract().response();

        String jsonToken= responseLogin.getBody().asString();
        tokens= JsonPath.from(jsonToken).get("token");
        System.out.println(tokens);

        String jsonId= responseLogin.getBody().asString();
        user_id= JsonPath.from(jsonId).get("user._id");
        System.out.println(user_id);
    }

    // user login validation
    @Test(priority = 5)
    public void userLoginValidate(){
        assertThat(responseLogin.path("user.email"), is(equalTo(email)));
        assertThat(responseLogin.path("user.name"), is(equalTo(name)));
        System.out.println("Validation successful");
    }



    // user login negative testCase
    @Test(priority = 6)
    public void userLoginNegative() {
        RestAssured.useRelaxedHTTPSValidation();
        JSONObject loginObject = new JSONObject();
        loginObject.put("email", "at@gmail.com");
        loginObject.put("password", "A1234556");

        responseLogin = given().spec(requestSpecification).
                filter(RequestLoggingFilter.logRequestTo(logDetails)).
                filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                body(loginObject.toString()).
                when().post("/user/login").
                then().statusCode(400).extract().response();
    }

}
