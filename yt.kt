import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = VideoAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            // Open the CSV file
            val inputStream = assets.open("videos.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Read the CSV file line by line
            val videos = mutableListOf<Video>()
            reader.forEachLine { line ->
                val video = Gson().fromJson(line, Video::class.java)
                videos.add(video)
            }

            // Close the CSV file
            reader.close()

            // Add the videos to the adapter
            adapter.videos = videos

            // Notify the adapter that the data has changed
            adapter.notifyDataSetChanged()
        }

        button = findViewById(R.id.button)
        button.setOnClickListener {
            // Get the selected video
            val selectedVideo = adapter.selectedVideo

            // If a video is selected, open it in YouTube
            if (selectedVideo != null) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$selectedVideo.id"))
                startActivity(intent)
            }
        }
    }
}

data class Video(val id: String, val title: String)

class VideoAdapter(private val activity: MainActivity) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    var videos = mutableListOf<Video>()
    var selectedVideo: Video? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]

        holder.titleTextView.text = video.title

        holder.itemView.setOnClickListener {
            selectedVideo = video
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }
}
