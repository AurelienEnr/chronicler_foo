package foo

import com.github.fsanaulla.core.model.InfluxFormatter
import com.github.fsanaulla.macros.Macros
import com.github.fsanaulla.macros.annotations.{field, tag, timestamp}


case class FooWithMacro(@tag product: String, @field qty: Int, @timestamp time: Long)

object FooWithMacro extends MeasurementName {
  implicit val format: InfluxFormatter[FooWithMacro] = Macros.format[FooWithMacro]
}
