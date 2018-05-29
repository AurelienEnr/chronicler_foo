package foo

import com.github.fsanaulla.core.model.{InfluxFormatter, Point}
import jawn.ast.JArray


case class FooWithoutMacro(product: String, qty: Int, time: Long) {

  def influxOneLiner: String = {
    Point(measurement = FooWithoutMacro.measurementName, time = time)
      .addTag("product", product)
      .addField("qty", qty)
      .serialize
  }

}

object FooWithoutMacro extends MeasurementName with Logging {

  implicit val writer = new InfluxFormatter[FooWithoutMacro] {
    def write(fe: FooWithoutMacro): String =
      fe.influxOneLiner
    def read(js: JArray): FooWithoutMacro =
      FooWithoutMacro(time=js.get(0).asLong, product=js.get(1).asString, qty=js.get(2).asInt)

  }
}
