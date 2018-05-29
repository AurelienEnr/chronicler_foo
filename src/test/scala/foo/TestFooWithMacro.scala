package foo

import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.model._
import jawn.ast.JArray
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

class TestFooWithMacro extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterAll with Logging {

  // todo: use a config file
  val influxAddress = "http://localhost:8086/"
  val dbName = "test_db"
  val influx: InfluxAsyncHttpClient = InfluxDB.connect(host = "localhost", port = 8086)
  val influxDB = influx.database(dbName)

  override def afterAll(): Unit = {
    Utils.clean(influx, dbName)
  }

  feature("foo") {

    import foo.FooWithMacro._

    scenario("measurementName corresponds to class name"){
      assert(FooWithMacro.measurementName == "FooWithMacro")
    }

    Utils.databaseIsUp(influx, influxAddress, dbName)

    scenario("write objects") {

      When("object")
      val p = FooWithMacro(product = this.getClass.getSimpleName, qty = -1, time = 10000000L)
      val s: String = FooWithMacro.format.write(p)

      // todo: I have no idea why there is no log here...
      println(s)
      println(s"written object: '$s'")

      Then("write macro")
      val foos = influx.measurement[FooWithMacro](dbName = dbName, measurementName = FooWithMacro.measurementName)
      val w: Future[Result] = foos.write(p)
      Utils.tryWrite(w)

    }


    val query = s"SELECT * FROM ${FooWithMacro.measurementName}"

    scenario("read untyped objects") {

      When("query is as before")

      Then("untyped read")
      val r: Future[QueryResult[JArray]] = influxDB.readJs(query)
      Utils.tryRead(r)

    }

    scenario("read typed objects") {

      When("query is as before")

      Then("typed read")
      val r: Future[QueryResult[FooWithMacro]] = influxDB.read[FooWithMacro](query)
      Utils.tryRead(r)

    }

  }

}
