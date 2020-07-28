package java8.play2.example.controllers;

import java8.play2.example.monitoring.ExampleJavaStatsD;

import play.mvc.Controller;
import play.mvc.Result;

import play.libs.Json;

public class ExampleDefaultApi extends Controller {

    @javax.inject.Inject
    ExampleJavaStatsD statsDClient;

    public Result defaultExampleIndex() {
        // Call some service's method and do some processing...
        return ok((Json.toJson("Hello.")));
    }

    public Result defaultExampleStatus() {
        return ok("OK");
    }
}
