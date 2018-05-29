package foo

trait MeasurementName {

  val measurementName = this.getClass.getSimpleName.replace("$", "")

}
