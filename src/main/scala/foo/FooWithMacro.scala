package foo

import com.github.fsanaulla.core.model.{InfluxFormatter, InfluxReader, InfluxWriter}
import com.github.fsanaulla.macros.Macros
import com.github.fsanaulla.macros.annotations.{field, tag, timestamp}


case class FooWithMacro(@tag product: String, @field qty: Int, @timestamp time: Long)

object FooWithMacro extends MeasurementName {

  implicit val reader: InfluxReader[FooWithMacro] = Macros.reader[FooWithMacro]
  implicit val writer: InfluxWriter[FooWithMacro] = Macros.writer[FooWithMacro]
  implicit val format: InfluxFormatter[FooWithMacro] = Macros.format[FooWithMacro]

}
