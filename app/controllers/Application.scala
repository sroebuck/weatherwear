package controllers

import play.api._
import play.api.mvc._
import play.libs.WS
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object Application extends Controller {
  
  val keyString = "1fd60563-da77-4bb9-88d5-0444be01310f"

  def index = Action {
    val responseFuture = WS.url("http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist")
      .setQueryParameter("key",keyString)
      .get
    val locationsJson = responseFuture.get.getBody
    val json = Json.parse(locationsJson)
    // val locations = (json \ "locations").as[List[JsValue]]
    // val idPlaceMappings = locations.map {
    //   location =>
    //   (location \ "id").as[String] -> (location \ "name").as[String]
    // }.toMap
    // Ok(views.html.index(Json.toJson(idPlaceMappings).toString))
    Ok(views.html.index(json.toString))
  }
  
}