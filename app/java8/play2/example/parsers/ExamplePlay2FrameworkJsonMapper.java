package java8.play2.example.parsers;

import play.libs.Json;

public class ExamplePlay2FrameworkJsonMapper {
    public Object read(String jsonString, Class clazz) {
        return Json.fromJson(Json.parse(jsonString), clazz);
    }
}
