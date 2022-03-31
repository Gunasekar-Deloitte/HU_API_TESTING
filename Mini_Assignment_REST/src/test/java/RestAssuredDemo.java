import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class RestAssuredDemo {
    @Test
    public void get_test_call(){
        given().
                baseUri("http://jsonplaceholder.typicode.com").
                header("Content-Type","application/json").
        when().
                get("/posts").
        then().
                statusCode(200).
                body("id[39]",equalTo(40)).
                body("userId[39]",equalTo(4)).
                body(containsString("title"));
    }

    @Test
    public void test_put_call(){
        File JsonData=new File("src\\test\\resources\\putdata.json");
        given().
                baseUri("http://reqres.in/api/users").
                body(JsonData).
                header("Content-Type","application/json").
                when().
                put("/users").
                then().
                statusCode(301).body("name",equalTo("Arun")).body("job",equalTo("Manager"));


    }

}
