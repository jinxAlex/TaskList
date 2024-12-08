package com.example.tasklist.fragment

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.tasklist.R
import android.widget.SearchView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class FragmentPagina(
    private val url: String,
    private val enviarPaginaActivityExtra:(String) -> Unit
) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_pagina, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        if(url.isNotEmpty()){
            val webView = view?.findViewById<WebView>(R.id.web_view)
            webView?.loadUrl(url)
        }
    }

    private fun setListeners() {
        val swipe = view?.findViewById<SwipeRefreshLayout>(R.id.swipe)
        val webView = view?.findViewById<WebView>(R.id.web_view) // Aqui no hay binding, por ende hay que buscar el id
        val searchView = view?.findViewById<SearchView>(R.id.search_view)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val busqueda = query.toString().lowercase().trim()
                if (android.util.Patterns.WEB_URL.matcher(busqueda).matches()) {
                    webView?.loadUrl(busqueda)

                    return true
                }
                val url = "https://www.google.es/search?q=${busqueda}"
                webView?.loadUrl(url)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                swipe?.isRefreshing = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if(url?.isNotEmpty() == true){
                    enviarPaginaActivityExtra(url)
                }
                swipe?.isRefreshing = false
            }
        }
        webView?.loadUrl("https://www.google.es")
    }
}