import java.time.LocalTime

class MovieFunctions {

    fun addMovie(name: String,genre: Genre,language: Language) {
        val movie=Movie(name,genre,language)
        Movie.movieList.add(movie)
    }

    fun addShowtime(movie: Movie,startTime:LocalTime,theater: Theater){
        val showtime=Showtime(movie,startTime,theater)
        Showtime.showtimeDatabase.add(showtime)
    }

    fun getShowTimeOfMovie(movie:Movie,theatreId:Int,showtime:LocalTime):Showtime{
        return Showtime.showtimeDatabase.first { it.movie == movie && it.theater.theaterId == theatreId && it.startTime == showtime }
    }
    fun getAllShowTimesForMovie(movie: Movie): List<Showtime> {
        return Showtime.showtimeDatabase.filter { it.movie == movie}
    }

    fun searchMovies(searchString:String):List<Movie>{
        val searchResults = mutableListOf<Movie>()
        for (movie in Movie.movieList) {
            if (movie.title.contains(searchString, ignoreCase = true)) {
                searchResults.add(movie)
            }
        }
        return searchResults
    }

}

class BookingFunction{
    fun addBooking(user:User,selectedShowTime:Showtime,seatList:List<Pair<Char,Int>>,noOFTicket:Int){
        val currentBooking=BookingDetails(BookingDetails.bookingHistoryList.size,selectedShowTime,seatList,noOFTicket*selectedShowTime.theater.price,user)
        if(BookingDetails.bookingHistoryList.containsKey(user)){
            BookingDetails.bookingHistoryList[user]?.add(currentBooking)
        }
        else{
            BookingDetails.bookingHistoryList[user] = mutableListOf(currentBooking)
        }
    }
}

class UserFunction {
    fun addUser(name: String,phoneNumber:Long,password:String) {
        val user = User(name,phoneNumber)
        User.userList[user] = password
    }
    fun checkUsername(name:String):Boolean{
        return User.userList.any {
            it.key.name==name
        }
    }
    fun checkPassword(name:String,password: String):Boolean{
        val storedPassword:String= User.userList.filter { it.key.name==name }.firstNotNullOf { it.value }
        return password == storedPassword
    }

}



class TheatreFunction {
    fun addTheatreToList(theaterId:Int, name: String, location: String, numberOfRows: Int, numberOfColumns:Int, price:Double) {
        val theater=Theater(theaterId,name,location,numberOfRows,numberOfColumns,price)
        Theater.TheaterList.add(theater)
    }
}







