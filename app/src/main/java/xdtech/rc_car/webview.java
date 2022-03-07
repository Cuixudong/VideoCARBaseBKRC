package xdtech.rc_car;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class webview extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView)findViewById(R.id.web);
        webView.setInitialScale(50);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.invokeZoomPicker();
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view,int progress){
                progressBar.setProgress(progress);
                progressBar.setVisibility(progress < 100 ? View.VISIBLE:View.GONE);
            }
        });
        webView.loadUrl("http://www.r8c.com");
    }

    @Override
    public void onBackPressed()
    {
        if (webView.canGoBack())
        {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
