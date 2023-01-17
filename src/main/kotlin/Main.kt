import java.time.LocalTime

fun main() {
    val movie1 = Movie("Avengers", Genre.ACTION,Language.TAMIL)
    val movie2 = Movie("RRR", Genre.ACTION,Language.TELUGU)
    val theater1 = Theater(100,"Cinema 1", "Los Angeles",5,10,120.0)
    val theater2 = Theater(102,"Cinema 2", "Los vegas",8,10,200.0)
    Theater.TheaterList.add(theater1)
    Theater.TheaterList.add(theater2)
    Movie.movieList.add(movie1)
    Movie.movieList.add(movie2)
    User.userList[User("paul",9765425672)]="2023"
    Showtime.showtimeDatabase.add(Showtime(movie1, LocalTime.of(2,15),theater1 ))
    Showtime.showtimeDatabase.add(Showtime(movie2, LocalTime.of(3,15),theater1))
    Showtime.showtimeDatabase.add(Showtime(movie1, LocalTime.of(3,15),theater2))
    Showtime.showtimeDatabase.add(Showtime(movie2, LocalTime.of(3,15),theater1))
    LoginView().start()
}