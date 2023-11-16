package com.example.ssjbeta2
import ArduinoProject
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.content.ContextCompat


// ProjectAdapter.kt
class ArduinoAdapter(private val projects: List<ArduinoProject>) : RecyclerView.Adapter<ArduinoAdapter.ProjectViewHolder>() {

    inner class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagesLayout: LinearLayout = view.findViewById(R.id.imagesLayout)

        val projectTitle: TextView = view.findViewById(R.id.projectTitle)
        val projectDescription: TextView = view.findViewById(R.id.projectDescription)
        val codeExplanation: TextView = view.findViewById(R.id.codeExplanation)

        init {
            view.setOnClickListener {
                val project = projects[adapterPosition]
                project.websiteUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                }
            }
        }



    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.projectTitle.text = project.title
        holder.projectDescription.text = project.description
        holder.codeExplanation.text = project.codeExplanation
        holder.codeExplanation.movementMethod = ScrollingMovementMethod()


        // Handle the images and their descriptions
        holder.imagesLayout.removeAllViews()  // Clear any previous views
        for (index in project.imageResourceIds.indices) {
            val imageResId = project.imageResourceIds[index]
            val aboutImage = project.aboutImages.getOrNull(index) ?: ""

            // Inflate the image_with_description layout
            val inflater = LayoutInflater.from(holder.imagesLayout.context)
            val imageLayout = inflater.inflate(R.layout.image_with_description, holder.imagesLayout, false)

            // Set the image and description
            val photoView: PhotoView = imageLayout.findViewById(R.id.photoView)
            val aboutImageView: TextView = imageLayout.findViewById(R.id.aboutImage)
            photoView.setImageResource(imageResId)
            aboutImageView.text = aboutImage

            // Add the layout to imagesLayout
            holder.imagesLayout.addView(imageLayout)
        }

        // Handle the website link
        if (!project.websiteUrl.isNullOrEmpty()) {
            val linkTextView = TextView(holder.itemView.context)
            linkTextView.text = project.linkText ?: "Here the Link"
            linkTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_light))
            linkTextView.paint.isUnderlineText = true
            linkTextView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(project.websiteUrl))
                holder.itemView.context.startActivity(browserIntent)
            }
            holder.imagesLayout.addView(linkTextView)
        }


        // Conditionally apply the text block style
        if (project.title == "Code Arduino") {  // or any other condition you want
            holder.projectDescription.setTextAppearance(R.style.CodeBlockStyle)
        } else {
            // Reset to default style if needed
            holder.projectDescription.setTextAppearance(android.R.style.TextAppearance_DeviceDefault)
        }

    }

    override fun getItemCount(): Int = projects.size
}
