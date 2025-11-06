import com.example.jobportal.SignIn
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginRetrofit {
    private const val BASE_URL = "https://jobseeker-backend-django.onrender.com/"

    val instance: SignIn by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SignIn::class.java)
    }
}
