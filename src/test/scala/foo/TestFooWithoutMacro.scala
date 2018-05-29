package foo

import com.github.fsanaulla.chronicler.async.{InfluxAsyncHttpClient, InfluxDB}
import com.github.fsanaulla.core.model._
import jawn.ast.JArray
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, GivenWhenThen, Matchers}

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global


class TestFooWithoutMacro extends FeatureSpec with GivenWhenThen with Matchers with BeforeAndAfterAll with Logging {

  // todo: use a config file
  val influxAddress = "http://localhost:8086/"
  val dbName = "test_db"
  val influx: InfluxAsyncHttpClient = InfluxDB.connect(host = "localhost", port = 8086)
  val influxDB = influx.database(dbName)

  override def afterAll(): Unit = {
    Utils.clean(influx, dbName)
  }


  feature("foo") {

    scenario("measurementName corresponds to class name"){
      assert(FooWithoutMacro.measurementName == "FooWithoutMacro")
    }

    Utils.databaseIsUp(influx, influxAddress, dbName)

    scenario("write objects") {

      When("object")
      val p = FooWithoutMacro(product = this.getClass.getSimpleName, qty = -1, time = 10000000L)
      log.warn(s"written object: '${p.influxOneLiner}'")

      Then("write")
      val w = influxDB.writeNative(p.influxOneLiner)
      Utils.tryWrite(w)

    }

    val query = s"SELECT * FROM ${FooWithoutMacro.measurementName}"

    scenario("read untyped objects") {

      When("query is as before")

      Then("untyped read")
      val r: Future[QueryResult[JArray]] = influxDB.readJs(query)
      Utils.tryRead(r)

    }

    scenario("read typed objects") {

      When("query is as before")

      Then("typed read")
      import foo.FooWithoutMacro._
      val r: Future[QueryResult[FooWithoutMacro]] = influxDB.read[FooWithoutMacro](query)
      Utils.tryRead(r)

    }

  }

}
