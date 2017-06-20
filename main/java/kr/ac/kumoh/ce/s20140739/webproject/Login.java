package kr.ac.kumoh.ce.s20140739.webproject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;


import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by 60974 on 2017-05-30.
 */

public class Login extends Activity {
    public static OAuthLogin mOAuthLoginModule;
    private static OAuthLoginButton mOAuthLoginButton;
    private static Context mContext;

    static String token;
    static String json="", cookieString="";
    loginDB logindb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        logindb = new loginDB();
        mContext = getApplicationContext();
        setContentView(R.layout.login);
        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                mContext
                , "XrgiqeOS9rEeJJ35jxmt"
                , "PyWxIsxzTq"
                , "6097461"
                //,OAUTH_CALLBACK_INTENT
                // SDK 4.1.4 버전부터는 OAUTH_CALLBACK_INTENT변수를 사용하지 않습니다.
        );
        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
        mOAuthLoginButton.setBgResourceId(R.drawable.naverbtn);
    }
    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);

                token = accessToken;
                logindb.execute();
                finish();

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }


    };
    public class loginDB extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            POST();

            return null;
        }

    }
    public static String POST() {
        try {



            String COOKIES_HEADER = "Set-Cookie";

            String apiURL = "http://192.168.123.107:3008/auth/naver";
//            String apiURL = "http://hmkcode.appspot.com/jsonservlet";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestMethod("POST");


            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setDefaultUseCaches(false);

            con.connect();




            JSONObject data = new JSONObject();
            data.accumulate("access_token", token);
            json = data.toString();


            OutputStream wr = con.getOutputStream();
            wr.write(json.getBytes("utf-8"));
            wr.flush();
            wr.close();

            Map<String, List<String>> headerFields = con.getHeaderFields();
            List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

            if(cookiesHeader != null) {
                for (String cookie : cookiesHeader) {
                    String cookieName = HttpCookie.parse(cookie).get(0).getName();
                    String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                    cookieString = cookieName + "=" + cookieValue;

                    CookieManager.getInstance().setCookie("http://192.168.123.107:3008", cookieString);

                }
            }


            int responseCode = con.getResponseCode();
            //         BufferedReader br;
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                //              br = new BufferedReader(new InputStreamReader(con.getInputStream()));




            } else {  // 에러 발생
                //             br = new BufferedReader(new InputStreamReader(con.getErrorStream()));


            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }


}
