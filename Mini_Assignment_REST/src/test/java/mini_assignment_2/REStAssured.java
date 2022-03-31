package mini_assignment_2;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class REStAssured {

    RequestSpecification requestSpecification;
    RequestSpecification requestSpecification1;

    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setup(){

        // request 1
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri("https://jsonplaceholder.typicode.com").
                addHeader("Content-Type", "application/json");
        requestSpecification = RestAssured.with().spec(requestSpecBuilder.build());

        //request 2
        RequestSpecBuilder requestSpecBuilder1 = new RequestSpecBuilder();
        requestSpecBuilder1.setBaseUri("https://reqres.in/api").
                addHeader("Content-Type", "application/json");
        requestSpecification1 = RestAssured.with().spec(requestSpecBuilder1.build());

        //respose 1
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON);
        responseSpecification = responseSpecBuilder.build();


    }
    @Test
    public void get_test_call(){
        io.restassured.RestAssured.useRelaxedHTTPSValidation();
        Response response =
        given().
            spec(requestSpecification).
        when().
            get("/posts").
        then().extract().response();
            assert (response.getStatusCode() == 200);
            assert (response.getContentType().contains("json"));
            JSONArray arr = new JSONArray(response.asString());
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if(obj.getInt("id")==40){
                    assert(obj.getInt("userId")==4);
                }
                assertThat(obj.getString("title"),true);
            }
        }

    @Test
    public void test_put_call(){
        io.restassured.RestAssured.useRelaxedHTTPSValidation();
        File JsonData=new File("src\\test\\resources\\putData.json");
        given().
            spec(requestSpecification1).
                body(JsonData).
        when().
            put("/users").
        then().
             spec(responseSpecification).
            body("name",equalTo("Arun")).
            body("job",equalTo("Manager"));
    }
}
