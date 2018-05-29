package foo

import com.github.fsanaulla.chronicler.async.InfluxAsyncHttpClient
import com.github.fsanaulla.core.model.{Result, _}
import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.scalatest.{FeatureSpec, GivenWhenThen}

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}
import scala.sys.process._
import scala.util.{Failure, Success, Try}


object Utils extends FeatureSpec with GivenWhenThen with Logging {

  val defaultDuration = 10 seconds

  def createDB(influx: InfluxAsyncHttpClient, dbName: String, waitingTime: Duration = defaultDuration): Unit = {
    val createDbResult: Future[Result] = influx.createDatabase(dbName)
    Try(Await.result(createDbResult, waitingTime)) match {
      case Success(s) =>
        log.warn(s.toString)
        assert(s.isSuccess)
      case Failure(exception) =>
        log.error(exception.getStackTrace.toString)
        assert(false)
    }
  }

  def checkDbExist(influxAddress: String, dbName: String): Unit= {
    implicit val formats = org.json4s.DefaultFormats
    val query = "query?q=show+databases"
    val cmd: Seq[String] = Seq("curl", "-H", "Content-Type: application/json", s"$influxAddress$query")
    val resultsRawString: String = cmd.!!
    val resultsRawMap: Map[String, Any] = parse(resultsRawString).extract[Map[String, Any]]
    val results: Map[String, Any] = resultsRawMap("results").asInstanceOf[Seq[Map[String, Any]]].head
    val subResults: Map[String, Any] = results("series").asInstanceOf[Seq[Map[String, Any]]].head

    assert(results("statement_id").asInstanceOf[BigInt] == 0)
    assert(subResults("name").asInstanceOf[String] == "databases")
    assert(subResults("values").asInstanceOf[Seq[Seq[String]]].exists(l => l.contains(dbName)))
  }

  def clean(influx: InfluxAsyncHttpClient, dbName: String, waitingTime: Duration = defaultDuration): Unit = {
    val clean = influx.database(dbName).readJs(s"DROP SERIES FROM /.*/")
    Try(Await.result(clean, waitingTime)) match {
      case Success(s) =>
        log.warn(s.toString)
        assert(s.isSuccess)
      case Failure(exception) =>
        log.error(exception.getStackTrace.toString)
        exception.printStackTrace()
        assert(false)
    }
  }

  def tryWrite(w: Future[Result], waitingTime: Duration = defaultDuration): Unit = {
    Try(Await.result(w, waitingTime)) match {
      case Success(s) =>
        log.warn(s"code=${s.code}, isSuccess=${s.isSuccess}}")
        assert(s.isSuccess)
      case Failure(exception) =>
        log.error(exception.getStackTrace.toString)
        exception.printStackTrace()
        assert(false)
    }
  }

  def tryRead[T](r: Future[QueryResult[T]], waitingTime: Duration = defaultDuration) = {
    Try(Await.result(r, waitingTime)) match {
      case Success(s) =>
        log.warn(s"code=${s.code}, isSuccess=${s.isSuccess}, result=${s.queryResult.mkString("\n\t", "\n\t", "\n")}")
        assert(s.isSuccess)
      case Failure(exception) =>
        log.error(exception.getStackTrace.toString)
        exception.printStackTrace()
        assert(false)
    }
  }

  var i = 0
  def databaseIsUp(influx: InfluxAsyncHttpClient, influxAddress: String, dbName: String): Unit = {
    i = i + 1
    scenario(s"database is up ($i)") {

      When("a database is created")
      Utils.createDB(influx: InfluxAsyncHttpClient, dbName)

      Then("a database exists")
      Utils.checkDbExist(influxAddress + "query?q=show+databases", dbName)

      Then("cleaning the measurements")
      Utils.clean(influx: InfluxAsyncHttpClient, dbName)

    }

  }

}
