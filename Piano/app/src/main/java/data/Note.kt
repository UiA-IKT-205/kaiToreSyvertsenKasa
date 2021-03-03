package data

data class Note(val value:String, val start:Long, val end: Double){

    override fun toString(): String {
        return "Note: $value, Started: $start, Ended: $end"
    }
}
