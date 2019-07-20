package darnassus.enums

object JobStatus extends Enumeration {
  type JobStatus = Value
  val Loading, Running, Stopped, Errored = Value
}
