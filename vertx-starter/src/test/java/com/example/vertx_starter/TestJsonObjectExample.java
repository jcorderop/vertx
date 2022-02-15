package com.example.vertx_starter;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestJsonObjectExample {

  @Test
  void jsonObjectCanBeMapped() {
    //given
    final JsonObject jsonObject = new JsonObject();

    //when
    jsonObject.put("id", 1);
    jsonObject.put("name", "vertx");
    jsonObject.put("isGood", true);

    //then
    final String encoded = jsonObject.encode();
    assertEquals("{\"id\":1,\"name\":\"vertx\",\"isGood\":true}", encoded);

    final JsonObject decoded =  new JsonObject(encoded);
    assertEquals(jsonObject, decoded);
  }

  @Test
  void jsonObjectCanBeCreateFromMap() {
    //given
    final Map<String, Object> map = new HashMap();

    //when
    map.put("id", 1);
    map.put("name", "vertx");
    map.put("isGood", true);

    //then
    final JsonObject jsonObject = new JsonObject(map);
    assertEquals(map, jsonObject.getMap());
    assertEquals(1, jsonObject.getInteger("id"));
    assertEquals("vertx", jsonObject.getString("name"));
    assertEquals(true, jsonObject.getBoolean("isGood"));
  }

  @Test
  void jsonArrayCanBeMap() {
    final JsonArray jsonObject = new JsonArray();
    jsonObject.add(new JsonObject().put("id",1));
    jsonObject.add(new JsonObject().put("id",2));
    jsonObject.add(new JsonObject().put("id",3));

    assertEquals("[{\"id\":1},{\"id\":2},{\"id\":3}]", jsonObject.encode());
  }

  @Test
  void canMapJavaObjects () {
    //given
    final Person person = new Person(1, "Jorge", true);

    //when
    final JsonObject jsonObject = JsonObject.mapFrom(person);
    final Person personFromJson = jsonObject.mapTo(Person.class);

    //then
    assertEquals(person.getId(), jsonObject.getInteger("id"));
    assertEquals(person.getName(), jsonObject.getString("name"));
    assertEquals(person.getGood(), jsonObject.getBoolean("good"));

    assertEquals(person.getId(), personFromJson.getId());
    assertEquals(person.getName(), personFromJson.getName());
    assertEquals(person.getGood(), personFromJson.getGood());

  }

  static class Person {
    private Integer id;
    private String name;
    private Boolean good;

    public Person() {

    }

    public Person(Integer id, String name, Boolean good) {
      this.id = id;
      this.name = name;
      this.good = good;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Boolean getGood() {
      return good;
    }

    public void setGood(Boolean good) {
      this.good = good;
    }

    @Override
    public String toString() {
      return "Person{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", isGood=" + good +
        '}';
    }
  }
}
