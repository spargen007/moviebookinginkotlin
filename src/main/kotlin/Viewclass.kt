

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.InputMismatchException
import java.util.Scanner

class LoginView{
    private val userFunction:UserFunction by lazy { UserFunction() }
    private val scanner by lazy {  Scanner(System.`in`)}
    fun start(){
        while(true){
            try {
                print(
                    """
        |+--------------------------------+
        |    Welcome To BookMyMovie App   |
        |+--------------------------------+
        |                                 |
        | 1. New Customer Registration    |
        | 2. Customer Login               |
        | 3. Admin Login                  |
        | 4. Exit                         |
        |+--------------------------------+
        |Enter your Choice:""".trimMargin())
                when (Integer.parseInt(scanner.nextLine())) {
                    1 -> newCustomerRegistration()
                    2 -> customerLogin()
                    3 -> adminLogin()
                    4 -> return
                    else -> println("Invalid option")
                }
            } catch (e:NumberFormatException){
                println("Enter correct option!!!!\n")
            }
            catch (e:Exception){
                println("${e.message} ${e.printStackTrace()}")
            }
        }
    }
    private fun newCustomerRegistration(){
        while (true) {
            val password: String
            val phoneNumber: Long
            val username:String
            try {
                println("Welcome to Registration Page.......")
                print("Enter Username:")
                username= scanner.nextLine()
                if (userFunction.checkUsername(username)) {
                    println("Already Exit !! Re-Enter it!!")
                    continue
                } else {
                    print("Enter PhoneNumber:")
                    phoneNumber = scanner.nextLine().toLong()
                    print("Enter Password:")
                    password = scanner.nextLine()
                }
                userFunction.addUser(username,phoneNumber,password)
            }catch (e:NumberFormatException){
                println("Enter Value Correctly")
            }
            catch (e:InputMismatchException){
                println("Enter correct Phone number")
            }
            catch (e:Exception){
                println("$e.message ${e.printStackTrace()}")
            }
            break
        }
    }
    private fun customerLogin() {
        while (true) {
            val password: String
            val username: String
            try {
                println("Welcome to Login Page.......")
                print("Enter Username:")
                username = scanner.nextLine()
                if (userFunction.checkUsername(username)) {
                    print("Enter Password:")
                    password = scanner.nextLine()
                    if (userFunction.checkPassword(username, password)) {
                        val user = User.userList.keys.find { it.name == username }
                        user?.let { UserView().start(it) }
                        break
                    } else {
                        println("Invalid password")
                        continue
                    }
                } else {
                    println("Invalid Username!!! Re-enter it!")
                }

            }catch (e:Exception){
                println(e.message)
            }
        }
    }
    private fun adminLogin() {
        print("Enter username:")
        val username=scanner.nextLine()
        print("Enter password:")
        val password=scanner.nextLine()
        val admin=Admin()
        if(admin.username==username && admin.password==password){
            AdminView().start(admin)
        }
        else{
            println("Invalid Credentials!!!1")
        }
    }


}

class UserView {
    private val movieFunctions:MovieFunctions by lazy { MovieFunctions() }
    private val bookingFunction:BookingFunction by lazy { BookingFunction() }
    private val movieView:MovieView by lazy { MovieView()}
    private val bookingView:BookingsView by lazy {  BookingsView()}
    private val scanner by lazy {  Scanner(System.`in`)}

    fun start(user: User) {
        while (true) {
            try {
                print(
                    """Welcome to BookMyMovie App!,!.
                    |Hi, ${user.name}
                    |1.View My Account
                    |2.Book a Ticket
                    |3.Display Now Showing Movies
                    |4.Booking History
                    |5.Exit               
                |Enter your Choice:""".trimMargin()
                )

                when (Integer.parseInt(scanner.nextLine())) {
                    1 -> viewMyProfile(user)
                    2 -> bookTicket(user)
                    3 -> movieView.displayMovies()
                    4 -> viewBookingHistory(user)
                    5 -> return
                    else -> println("Invalid option")
                }

            } catch (e: NumberFormatException) {
                println("Enter correct option!!!!\n")
            } catch (e: IndexOutOfBoundsException) {
                println("Enter only available option\n")
            } catch (e: Exception) {
                println("${e.message} ${e.printStackTrace()}")
            }
        }
    }


    private fun viewMyProfile(user: User) {
        user.let {
            println(
                """
                |Username   --> ${it.name}
                |PhoneNumber--> ${it.phoneNumber}
                |Password   --> ${User.userList[user]}""".trimMargin()
            )
            println()
        }

    }


    private fun bookTicket(user: User) {
        var noOFTicket: Int
        movieView.displayMovies()
        print("Enter id for Movie:")
        val movieId = Integer.parseInt(scanner.nextLine())
        val movie = Movie.movieList[movieId - 1]
        val movieShowTimes = movieFunctions.getAllShowTimesForMovie(movie)
        movieView.displayShowTimesOfMovie(movieShowTimes, movie)
        var theatreId: Int
        if (Showtime.showtimeDatabase.any {
                it.movie == movie
            }) {
            while (true) {
                try {
                    print("Enter theatre id:")
                    theatreId = Integer.parseInt(scanner.nextLine())
                    if (Showtime.showtimeDatabase.any {
                            it.movie == movie && it.theater.theaterId == theatreId
                        }) {
                        break
                    } else {
                        throw Exception("Enter valid theatre id!!!!")
                    }

                } catch (e: NumberFormatException) {
                    println("Enter valid number!!!!")
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            var timeInput: String
            var time: LocalTime
            while (true) {
                try {
                    print("Enter timing (HH:mm -- (eg:02:15) ): ")
                    timeInput = scanner.nextLine()
                    time = LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("HH:mm"))
                    if (Showtime.showtimeDatabase.any {
                            it.theater.theaterId == theatreId && it.startTime == time && it.movie==movie
                        }) {
                        break
                    } else {
                        throw Exception("Invalid Showtime")
                    }
                } catch (e: DateTimeParseException) {
                    println("Enter correct time format!!!")
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            val selectedShowTime = movieFunctions.getShowTimeOfMovie(movie, theatreId, time)
            movieView.displaySeatsForShowTimeOfMovie(selectedShowTime)
            println("*# Price of a Ticket -- ₹ ${selectedShowTime.theater.price} -- #*")
            while (true) {
                val availableSeat = selectedShowTime.seatMap.filter { it.value }.count()
                try {
                    print(
                        """Available Seat:${availableSeat}
How many Ticket do You Want?"""
                    )
                    noOFTicket = Integer.parseInt(scanner.nextLine())
                    if (noOFTicket > availableSeat || noOFTicket < 1) {
                        throw Exception("You Entered Invalid no of ticket !!! ")
                    }
                    break
                } catch (e: NumberFormatException) {
                    println("Enter valid no of ticket !!!")
                } catch (e: Exception) {
                    println(e.message)
                }
            }
            val seatList = mutableListOf<Pair<Char, Int>>()
            for (i in 0 until noOFTicket) {
                while (true) {
                    try {

                        println("Enter RowName and ColumnNumber for ticket ${i + 1}:")
                        val input = scanner.nextLine()
                        val charInput = input[0].uppercaseChar()
                        val intInput = input.substring(1).toInt()
                        val inputPair = Pair(charInput, intInput)
                        if (charInput > ('A' + (selectedShowTime.theater.numberOfRows - 1)) || intInput > selectedShowTime.theater.numberOfColumns - 1 || selectedShowTime.seatMap[inputPair] == false) {
                            println("The Selected Seat $inputPair is not Available\nPlease select any other Seat.")
                            continue
                        } else {
                            selectedShowTime.seatMap[inputPair] = false
                            seatList.add(inputPair)
                            break
                        }
                    } catch (e: NumberFormatException) {
                        println("Enter valid seat-number format!!!")
                    }
                }
            }
            bookingFunction.addBooking(user, selectedShowTime, seatList, noOFTicket)
            println("***** Booking is Successful *****")

        }else{
            println()
            println("Sorry no shows alerted for ${movie.title}\nPlease Try again later!!")
            println()
        }
    }


    private fun viewBookingHistory(user: User) {
        if (BookingDetails.bookingHistoryList.isNotEmpty()) {
            bookingView.displayBookingHistory(user)
            print("Enter Booking Id:")
            val bookingId = Integer.parseInt(scanner.nextLine())
            if (BookingDetails.bookingHistoryList[user]!!.any { it.bookingId == bookingId }) {
                bookingView.displayBookingDetails(bookingId, user)
            } else {
                println("Invalid Booking Id!!\n")
            }
        } else {
            println("No Bookings Till Now !!\njust book a movie and experience a visual threat!\n")
        }
    }
}

class AdminView {
    private val theatreFunction:TheatreFunction by lazy { TheatreFunction()}
    private val movieFunctions:MovieFunctions by lazy { MovieFunctions()}
    private val bookingView:BookingsView by lazy { BookingsView()}
    private val movieView:MovieView by lazy { MovieView()}
    private val scanner by lazy {  Scanner(System.`in`)}
    fun start(admin: Admin) {
        while (true) {
            try {
                println()
                print(
                    """Welcome to BookMyMovie App!,!.
                    |Hi, Admin
                    |1.Add New Theatre
                    |2.Add Movie
                    |3.Add Showtime for Movie
                    |4.View All Booking Details
                    |5.Exit               
                |Enter your Choice:""".trimMargin()
                )

                when (Integer.parseInt(scanner.nextLine())) {
                    1 -> addTheatre()
                    2 -> addMovie()
                    3 -> addShowtime()
                    4 -> viewAllBookingDetails(admin)
                    5 -> return
                    else -> println("Invalid option")
                }

            } catch (e: NumberFormatException) {
                println("Enter correct option!!!!\n")
            } catch (e: ArrayIndexOutOfBoundsException) {
                println("Enter Valid MovieId")
            } catch (e: Exception) {
                println("${e.message} ${e.printStackTrace()}")
            }
        }
    }

    private fun addTheatre() {
        print("Enter Theatre name:")
        val theaterName: String = scanner.nextLine()
        print("Enter Theatre Id:")
        val theatreId: Int = Integer.parseInt(scanner.nextLine())
        print("Enter Theatre location:")
        val theatreLocation: String = scanner.nextLine()
        print("Enter No of Rows in theatre:")
        val noOfRows: Int = Integer.parseInt(scanner.nextLine())
        print("Enter No of Columns in theatre:")
        val noOfColumn: Int = Integer.parseInt(scanner.nextLine())
        print("Enter Price of Ticket in theatre:")
        val ticketPrice: Double = scanner.nextLine().toDouble()
        with(theatreFunction) {
            addTheatreToList(
                theatreId,
                theaterName,
                theatreLocation,
                noOfRows,
                noOfColumn,
                ticketPrice
            )
        }

    }

    private fun addMovie() {
        lateinit var genre: Genre
        lateinit var language: Language
        print("Enter Movie Name:")
        val movieName: String = scanner.nextLine()
        println("Available Genre:")
        Genre.values().forEach {
            println(it)
        }
        println("Enter a Genre:")
        val genreInput = scanner.nextLine()
        try {
            genre = Genre.valueOf(genreInput!!.uppercase())
        } catch (e: IllegalArgumentException) {
            println("Invalid genre entered")
        }
        println("Available Languages:")
        Language.values().forEach {
            println(it)
        }
        println("Enter a Language:")
        val languageInput = scanner.nextLine()
        try {
            language = Language.valueOf(languageInput!!.uppercase())
        } catch (e: IllegalArgumentException) {
            println("Invalid Language entered")
        }
        movieFunctions.addMovie(movieName, genre, language)

    }

    private fun addShowtime() {
        movieView.displayMovies()
        print("Enter id for Movie to add showtime:")
        val movieId = Integer.parseInt(scanner.nextLine())
        val movie = Movie.movieList[movieId - 1]
        var theatreId: Int
        while (true) {
            try {
                println("TheatreId -- TheatreName")
                Theater.TheaterList.forEach {
                    println("${it.theaterId}       -- ${it.name}")
                }
                print("Enter theatre id:")
                theatreId = Integer.parseInt(scanner.nextLine())
                if (Theater.TheaterList.any {
                        it.theaterId == theatreId
                    }) {
                    break
                } else {
                    throw Exception("Enter valid theatre id!!!!")
                }

            } catch (e: NumberFormatException) {
                println("Enter valid number!!!!")
            } catch (e: Exception) {
                println(e.message)
            }
        }
        val theater = Theater.TheaterList.find { it.theaterId == theatreId }
        var time: LocalTime
        var timeInput: String
        while (true) {
            try {
                print("Enter timing (HH:mm -- (eg:02:15) ): ")
                timeInput = scanner.nextLine()
                time = LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("HH:mm"))

                if (Showtime.showtimeDatabase.any {
                        it.theater.theaterId == theatreId && it.startTime == time
                    }) {
                    println("The entered showtime:${time} is not available in $theater")
                    continue
                } else {
                    break
                }
            } catch (e: DateTimeParseException) {
                println("Enter correct time format!!!")
            } catch (e: Exception) {
                println(e.message)
            }
        }
        movieFunctions.addShowtime(movie, time, theater!!)
    }

    private fun viewAllBookingDetails(admin: Admin) {
        if (BookingDetails.bookingHistoryList.isNotEmpty()) {
            bookingView.displayBookingHistory(admin)
            print("Enter Booking Id:")
            val bookingId = Integer.parseInt(scanner.nextLine())
            if (BookingDetails.bookingHistoryList.any { user ->
                    user.value.any { it.bookingId == bookingId }
                }) {
                bookingView.displayBookingDetails(bookingId, admin)
            } else {
                println("Invalid Booking Id!!\n")
            }
        } else {
            println("No Bookings Till Now !!")
        }
    }

}

class MovieView {
    fun displayMovies() {
        println()
        println("Now playing:")
        Movie.movieList.forEachIndexed { index: Int, movie: Movie ->
            println("${index + 1}. ${movie.title} (${movie.genre}) -${movie.language}")
        }
        println()
    }

    fun displayShowTimesOfMovie(showTimes: List<Showtime>, movie: Movie) {
        val groupedTheater = showTimes.groupBy { it.theater }
        for ((theater, showtime) in groupedTheater) {
            println("Theater Name --> ${theater.name}:: id- ${theater.theaterId}")
            println("Movie Name --> ${movie.title}")
            showtime.forEach {
                print("* ${it.startTime} ")
            }
            println()
        }
    }

    fun displaySeatsForShowTimeOfMovie(showtime: Showtime) {
        println(
            """MovieName:${showtime.movie.title}
            |TheaterName:${showtime.theater.name}
            |Showtime:${showtime.startTime}""".trimMargin()
        )
        println("***************------Screen-------***************** ")
        for (row in 0 until showtime.theater.numberOfRows) {
            print("${'A' + row}: ")
            for (column in 0 until showtime.theater.numberOfColumns) {
                if (showtime.seatMap[Pair('A' + row, column)] == true) {
                    print("\u001B[32m$column\u001B[0m | ")
                } else {
                    print("\u001B[31mX\u001B[0m | ")
                }
            }
            println()
        }

    }
}

class BookingsView {

    fun <T>displayBookingHistory(t:T){
        when(t){
            is User -> {        println(
                """
 Hi, ${t.name}
 |Booking History:
        """.trimMargin()
            )
                BookingDetails.bookingHistoryList[t]?.forEach {
                    println("${it.bookingId} -- ${it.showtime.movie} -- ${it.showtime.theater.name}")
                }

            }
            is Admin -> {
                println(
                    """
 Hi, ${t.username}
 |Booking History:
        """.trimMargin()
                )
                BookingDetails.bookingHistoryList.forEach { user ->
                    println("${user.key.name}--${user.key.phoneNumber}")
                    user.value.forEach {
                        println("${it.bookingId} -- ${it.showtime.movie} -- ${it.showtime.theater.name}")
                    }
                }

            }
        }
    }
    fun <T> displayBookingDetails(id:Int,t:T){
        var bookingDetails:BookingDetails?=null
        when(t){
            is User ->{ bookingDetails = BookingDetails.bookingHistoryList[t]?.find { it.bookingId == id }!!
            }
            is Admin ->{ bookingDetails = BookingDetails.bookingHistoryList.flatMap {
                it.value
            }.first { it.bookingId == id }}
            else -> println("Not Authorized")
        }
        bookingDetails?.let { it ->
            println()
            println(
                """
                Booking id:${it.bookingId}
                Theatre Name:${it.showtime.theater.name}
                Movie :${it.showtime.movie.title}--${it.showtime.movie.genre}--${it.showtime.movie.language}
                ShowTime:${it.showtime.startTime}
            """.trimIndent()
            )
            print("Seats:")

            it.seats.forEach {
                print("${it.first}${it.second}, ")
            }
            println()
            println(
                """
                |Ticket Price:${it.showtime.theater.price}
                |No.of Tickets:${it.seats.count()}
                |Total Price:${it.totalCost}
                
            """.trimMargin()
            )
        }
        println()

    }
}

