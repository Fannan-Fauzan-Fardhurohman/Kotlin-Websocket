package id.fannan.websocketpractice.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import id.fannan.websocketpractice.R
import id.fannan.websocketpractice.helpers.Constants
import id.fannan.websocketpractice.services.WebSocketServices
import id.fannan.websocketpractice.ui.vm.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    private lateinit var webSocketListener: WebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        webSocketListener = WebSocketServices(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageET = view.findViewById<EditText>(R.id.et_message)
        val sendMessageButton = view.findViewById<ImageButton>(R.id.btn_send)
        val connectButton = view.findViewById<Button>(R.id.btn_connect)
        val disconnectButton = view.findViewById<Button>(R.id.btn_disconnect)
        val statusTV = view.findViewById<TextView>(R.id.tv_status)
        val messageTV = view.findViewById<TextView>(R.id.tv_message)

        viewModel.socketStatus.observe(viewLifecycleOwner) {
            statusTV.text = if (it) "Connected" else "Disconnected"
        }

        var text = ""
        viewModel.messages.observe(viewLifecycleOwner) {
            text += "${if (it.first) "You : " else "Other : "}${it.second}\n"

            messageTV.text = text
        }


        connectButton.setOnClickListener {
            webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)
        }

        disconnectButton.setOnClickListener {
            webSocket?.close(1000,"Canceled Manually")
        }

        sendMessageButton.setOnClickListener{
            webSocket?.send(messageET.text.toString())
            viewModel.addMessages(Pair(true,messageET.text.toString()))
        }


    }

    private fun createRequest(): Request {
        val webSocketUrl =
            "wss://${Constants.CLUSTER_ID}.piesocket.com/v3/1?api_key=${Constants.API_KEY}"
        return Request.Builder()
            .url(webSocketUrl)
            .build()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        okHttpClient.dispatcher.executorService.shutdown()
    }

    companion object{
        fun newInstance() = MainFragment()
    }
}