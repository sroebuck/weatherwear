package controllers

import play.api._
import play.api.mvc._
import play.libs.WS
import play.api.libs.json.Json
import play.api.libs.json.JsValue

object Application extends Controller {
  
  private val metOfficeKey = "1fd60563-da77-4bb9-88d5-0444be01310f"

  private lazy val locationsJson = {
    val responseFuture = WS.url("http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist")
      .setQueryParameter("key", metOfficeKey)
      .get
    responseFuture.get.getBody
  }

  def index = Action {
    Ok(views.html.index(""))
  }

  def locationsFeed = Action {
    Ok(locationsJson)
  }
  
}
