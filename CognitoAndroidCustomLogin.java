package com.example.rishabh.example;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.auth.AWSAbstractCognitoIdentityProvider;
import com.amazonaws.auth.AWSBasicCognitoIdentityProvider;
import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSEnhancedCognitoIdentityProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClient;
import com.amazonaws.services.cognitoidentity.model.Credentials;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import com.amazonaws.services.cognitoidentity.model.GetCredentialsForIdentityResult;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenRequest;
import com.amazonaws.services.cognitoidentity.model.GetOpenIdTokenResult;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CognitoCustomLogin extends AppCompatActivity {
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    GetCredentialsForIdentityResult getCredentialsResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cognito_custom_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new sendUserDetailsAndReadDataSecurely().execute();
    }
    String doPostRequest(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    class sendUserDetailsAndReadDataSecurely extends AsyncTask<Void,  Void,  Void> {
        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", "abc");
                jsonObject.put("password", "cde");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String identityId = null;
            String token = null;
            String val = null;
            try {
                val = doPostRequest("http://localhost:5000", jsonObject.toString());
                JSONObject jsonObject1 = new JSONObject(val);
                identityId = jsonObject1.getString("IdentityId");
                token = jsonObject1.getString("Token");
            } catch (Exception ex) {
               
            }

            Map<String, String> logins = new HashMap();
            logins.put("cognito-identity.amazonaws.com", token);
            GetCredentialsForIdentityRequest getCredentialsRequest = new GetCredentialsForIdentityRequest().withIdentityId(identityId).withLogins(logins);
            AmazonCognitoIdentityClient cognitoIdentityClient = new AmazonCognitoIdentityClient(new AnonymousAWSCredentials());
            getCredentialsResult = cognitoIdentityClient.getCredentialsForIdentity(getCredentialsRequest);

            BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                    getCredentialsResult.getCredentials().getAccessKeyId(),
                    getCredentialsResult.getCredentials().getSecretKey(),
                    getCredentialsResult.getCredentials().getSessionToken());

            AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(sessionCredentials);
            DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

            PostDataClass postToFind = new PostDataClass();
            postToFind.setemployee_id("123");

            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withHashKeyValues(postToFind)
                    .withScanIndexForward(false)
                    .withConsistentRead(false);

            List<PostDataClass> post_data_class_list = mapper.query(PostDataClass.class, queryExpression);
            for (PostDataClass postDataClass : post_data_class_list) {
                Log.d("my_info", postDataClass.toString() + "#" + postDataClass.getComments());
            }
            return null;
        }
    }
}