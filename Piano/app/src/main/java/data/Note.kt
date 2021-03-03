package data

data class Note(val value:String, val start:String, val end: Double){

    override fun toString(): String {
        return "Note $value was pressed and released, Started at time $start, Ended after $end seconds"
    }
}
