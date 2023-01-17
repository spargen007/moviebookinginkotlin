
import java.time.LocalTime
data class Theater(
    val theaterId:Int,
    val name: String,
    val location: String,
    val numberOfRows: Int,
    val numberOfColumns:Int,
    val price:Double){
    companion object{
        val TheaterList = mutableListOf<Theater>()
    }
}

data class Movie(
    val title: String,
    val genre: Genre,
    val language:Language){
    companion object {
        val movieList = mutableListOf<Movie>()
    }
}

data class User(
    val name:String,
    val phoneNumber:Long
){
    companion object{
        val userList = mutableMapOf<User,String>()
    }
}

data class Admin(
    val username:String="bookmymovie",
    val password: String="2023"
)

data class Showtime(
    val movie: Movie,
    val startTime: LocalTime,
    val theater: Theater){
    companion object {
        val showtimeDatabase = mutableListOf<Showtime>()
    }
    val seatMap= mutableMapOf<Pair<Char,Int>,Boolean>()
    init {
        for(row in 0 until theater.numberOfRows){
            for(column in 0 until theater.numberOfColumns){
                seatMap[Pair('A' + row,column)]=true
            }
        }
    }
}

data class BookingDetails(
    val bookingId:Int,
    val showtime: Showtime,
    val seats: List<Pair<Char,Int>>,
    val totalCost: Double,
    val user:User){
    companion object{
        val bookingHistoryList= mutableMapOf<User,MutableList<BookingDetails>>()
    }
}

enum class Genre {
    ACTION, COMEDY, HORROR, DRAMA, THRILLER
}
enum class Language{
    TAMIL,ENGLISH,HINDI,TELUGU,MALAYALAM
}

