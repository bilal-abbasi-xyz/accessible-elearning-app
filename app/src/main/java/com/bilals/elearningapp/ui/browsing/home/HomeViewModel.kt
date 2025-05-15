import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bilals.elearningapp.tts.SpeechService

class HomeViewModel : ViewModel() {
    private val _buttonLabels = MutableLiveData(listOf("Browse Courses", "Public Forum", "Settings", "Training", "Reports"))
    val buttonLabels: LiveData<List<String>> = _buttonLabels

    fun announceHomeScreen(context: Context) {
        SpeechService.announce(context, "Home Screen.")
    }

}
