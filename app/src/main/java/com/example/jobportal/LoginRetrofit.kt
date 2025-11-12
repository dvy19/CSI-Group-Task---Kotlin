import com.example.jobportal.SignIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object LoginRetrofit {
    private const val BASE_URL = "https://jobseeker-backend-django.onrender.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Wait up to 30 seconds to connect
        .readTimeout(60, TimeUnit.SECONDS)    // Wait up to 30 seconds for data
        .writeTimeout(60, TimeUnit.SECONDS)   // Wait up to 30 seconds to send data
        .retryOnConnectionFailure(true)       // Retry on connection failures
        .build()

    val instance: SignIn by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(SignIn::class.java)
    }
}
