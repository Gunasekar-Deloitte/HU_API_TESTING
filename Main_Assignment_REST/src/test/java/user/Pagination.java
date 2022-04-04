package user;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import static io.restassured.RestAssured.given;

public class Pagination {

    PrintStream logDetails;

    {
        try {
            logDetails = new PrintStream(new File("src\\LogFileForPagination.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    // request and response specBuilder setup
    @BeforeClass
    public void buildersSetup(){

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri("https://api-nodejs-todolist.herokuapp.com").
                addHeader("Content-Type", "application/json").
                addHeader("Authorization", "Bearer " + RegisterUser.tokens);
        requestSpecification = RestAssured.with().spec(requestSpecBuilder.build());

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON).expectStatusCode(200);
        responseSpecification = responseSpecBuilder.build();

    }
    // pagination with limit 2
    @Test(priority = 9)
    public void paginationLimit_2(){
        System.out.println("Page nation with limit 2");
                given().spec(requestSpecification).
                        filter(RequestLoggingFilter.logRequestTo(logDetails)).
                        filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                        queryParam("limit",2).
                when().get("/task").
                then().statusCode(200).log().all();
    }

    // pagination with limit 5
    @Test(priority = 10)
    public void paginationLimit_5(){
        System.out.println("Page nation with limit 5");
                given().spec(requestSpecification).
                        filter(RequestLoggingFilter.logRequestTo(logDetails)).
                        filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                        queryParam("limit",5).
                when().get("/task").
                then().spec(responseSpecification).log().all();
    }

    // pagination with limit 10
    @Test(priority = 11)
    public void paginationLimit_10(){
                given().spec(requestSpecification).
                        filter(RequestLoggingFilter.logRequestTo(logDetails)).
                        filter(ResponseLoggingFilter.logResponseTo(logDetails)).
                        queryParam("limit",10).
                when().get("/task").
                then().spec(responseSpecification).log().all();
    }

}
